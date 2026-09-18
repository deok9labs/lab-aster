package com.deok9labs.aster.ember.adapter.out.persistence;

import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySlotsEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySlotsId;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsEntity;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.AvailabilitySubmissionsId;
import com.deok9labs.aster.ember.adapter.out.persistence.entity.MembersEntity;
import com.deok9labs.aster.ember.application.port.out.LoadCurrentSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.AvailabilitySlotsJpaRepository;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.AvailabilitySubmissionsJpaRepository;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.MembersJpaRepository;
import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Application의 일정 persistence 계약을 JPA로 구현한다. */
@Component
public class EmberPersistenceAdapter implements LoadCurrentSchedulePort, SaveMemberSchedulePort {

    private final MembersJpaRepository membersRepository;
    private final AvailabilitySubmissionsJpaRepository submissionsRepository;
    private final AvailabilitySlotsJpaRepository slotsRepository;

    public EmberPersistenceAdapter(
            MembersJpaRepository membersRepository,
            AvailabilitySubmissionsJpaRepository submissionsRepository,
            AvailabilitySlotsJpaRepository slotsRepository) {
        this.membersRepository = membersRepository;
        this.submissionsRepository = submissionsRepository;
        this.slotsRepository = slotsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentScheduleData loadCurrentSchedule(WeekPeriod week) {
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
        Map<MemberDate, List<LocalTime>> timesByMemberDate = new LinkedHashMap<>();
        slotsRepository.findAllByIdWeekStartOrderByIdAvailableDateAscIdSlotTimeAsc(week.start())
                .forEach(slot -> {
                    AvailabilitySlotsId id = slot.getId();
                    if (!activeMemberIds.contains(id.getMemberId())) {
                        return;
                    }
                    timesByMemberDate.computeIfAbsent(
                                    new MemberDate(id.getMemberId(), id.getAvailableDate()),
                                    ignored -> new ArrayList<>())
                            .add(id.getSlotTime());
                });
        List<AvailabilityData> availability = timesByMemberDate.entrySet().stream()
                .map(entry -> new AvailabilityData(
                        entry.getKey().memberId(), entry.getKey().date(), entry.getValue()))
                .toList();
        return new CurrentScheduleData(members, availability);
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
        slotsRepository.deleteMemberWeekSlots(memberId, week.start());
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

        // 검증된 domain slot만 JPA entity로 변환해 한 번에 저장한다.
        List<AvailabilitySlotsEntity> entities = slots.stream()
                .map(slot -> new AvailabilitySlotsEntity(new AvailabilitySlotsId(
                        memberId, week.start(), slot.date(), slot.time())))
                .toList();
        slotsRepository.saveAll(entities);
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
