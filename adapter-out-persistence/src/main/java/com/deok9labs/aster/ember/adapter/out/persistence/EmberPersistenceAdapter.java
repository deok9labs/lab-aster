package com.deok9labs.aster.ember.adapter.out.persistence;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsId;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.MembersEntity;
import com.deok9labs.aster.ember.application.port.out.LoadSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.AvailabilitySubmissionsJpaRepository;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.MembersJpaRepository;
import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/** Application의 일정 persistence 계약을 JPA와 PostgreSQL JDBC로 구현한다. */
@Component
public class EmberPersistenceAdapter implements LoadSchedulePort, SaveMemberSchedulePort {

    private final MembersJpaRepository membersRepository;
    private final AvailabilitySubmissionsJpaRepository submissionsRepository;
    private final JdbcTemplate jdbcTemplate;

    public EmberPersistenceAdapter(
            MembersJpaRepository membersRepository,
            AvailabilitySubmissionsJpaRepository submissionsRepository,
            JdbcTemplate jdbcTemplate) {
        this.membersRepository = membersRepository;
        this.submissionsRepository = submissionsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleData loadSchedule(WeekPeriod week) {
        // 제출 상태를 팀원별로 색인해 목록 변환 중 추가 조회가 발생하지 않게 한다.
        List<AvailabilitySubmissionsEntity> submissions =
                submissionsRepository.findAllByIdWeekStart(week.start());
        Map<Integer, AvailabilitySubmissionsEntity> submissionByMember = new LinkedHashMap<>();
        submissions.forEach(submission ->
                submissionByMember.put(submission.getId().getMemberId(), submission));

        // 비활성 팀원은 과거 제출과 slot이 남아 있어도 현재 화면의 집계 대상이 아니다.
        List<MembersEntity> activeMemberEntities = membersRepository.findActiveMembersInDisplayOrder();
        Set<Integer> activeMemberIds = activeMemberEntities.stream()
                .map(MembersEntity::getId)
                .collect(Collectors.toUnmodifiableSet());
        List<MemberData> members = activeMemberEntities.stream()
                .map(member -> toMemberData(member, submissionByMember.get(member.getId())))
                .toList();

        // DB의 slot별 행을 API가 소비하는 팀원·날짜별 시간 목록으로 묶는다.
        Map<MemberDate, List<ScheduleTime>> timesByMemberDate = new LinkedHashMap<>();
        // JDBC의 LocalTime은 24:00을 00:00과 구분하지 못하므로 PostgreSQL에서 문자열로 변환해 읽는다.
        jdbcTemplate.query(
                """
                        select member_id, available_date, to_char(slot_time, 'HH24:MI') as slot_time
                        from availability_slots
                        where week_start = ?
                        order by available_date, slot_time
                        """,
                resultSet -> {
                    int memberId = resultSet.getInt("member_id");
                    if (!activeMemberIds.contains(memberId)) {
                        return;
                    }
                    timesByMemberDate.computeIfAbsent(
                                    new MemberDate(
                                            memberId,
                                            resultSet.getObject("available_date", LocalDate.class)),
                                    ignored -> new ArrayList<>())
                            .add(ScheduleTime.parse(resultSet.getString("slot_time")));
                },
                week.start());
        List<AvailabilityData> availability = timesByMemberDate.entrySet().stream()
                .map(entry -> new AvailabilityData(
                        entry.getKey().memberId(), entry.getKey().date(), entry.getValue()))
                .toList();
        return new ScheduleData(members, availability);
    }

    @Override
    @Transactional
    public Optional<SavedScheduleData> replaceSchedule(
            int memberId,
            WeekPeriod week,
            List<ScheduleSlot> slots,
            LocalDateTime updatedAt) {
        // 존재하지만 비활성인 팀원도 새 제출을 만들 수 없도록 같은 조건으로 확인한다.
        if (!membersRepository.existsActiveMember(memberId)) {
            return Optional.empty();
        }

        // 현재 상태만 보존하므로 기존 slot을 hard delete한 뒤 요청 상태를 그대로 다시 만든다.
        jdbcTemplate.update(
                "delete from availability_slots where member_id = ? and week_start = ?",
                memberId,
                week.start());
        AvailabilitySubmissionsId submissionId =
                new AvailabilitySubmissionsId(memberId, week.start());
        AvailabilitySubmissionsEntity submission = submissionsRepository.findById(submissionId)
                .map(existing -> {
                    existing.advanceRevision(updatedAt);
                    return existing;
                })
                .orElseGet(() -> new AvailabilitySubmissionsEntity(submissionId, 1, updatedAt));
        // 새 제출의 복합키가 자식 slot의 외래키보다 먼저 DB에 존재하도록 즉시 flush한다.
        submissionsRepository.saveAndFlush(submission);

        // PostgreSQL time은 24:00을 보존하지만 JDBC LocalTime은 표현하지 못해 문자열을 명시적으로 cast한다.
        jdbcTemplate.batchUpdate(
                """
                        insert into availability_slots
                            (member_id, week_start, available_date, slot_time)
                        values (?, ?, ?, cast(? as time))
                        """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement statement, int index) throws SQLException {
                        ScheduleSlot slot = slots.get(index);
                        statement.setInt(1, memberId);
                        statement.setObject(2, week.start());
                        statement.setObject(3, slot.date());
                        statement.setString(4, slot.slotTime().toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return slots.size();
                    }
                });
        return Optional.of(new SavedScheduleData(submission.getRevision(), submission.getUpdatedAt()));
    }

    private MemberData toMemberData(
            MembersEntity member,
            AvailabilitySubmissionsEntity submission) {
        return new MemberData(
                member.getId(),
                member.getDisplayName(),
                member.getServerName(),
                member.getPositionCode(),
                submission != null,
                submission == null ? null : submission.getUpdatedAt());
    }

    private record MemberDate(int memberId, LocalDate date) {
    }
}
