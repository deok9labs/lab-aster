package com.deok9labs.aster.ember.application.service;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import com.deok9labs.aster.ember.application.port.in.GetCurrentScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
import com.deok9labs.aster.ember.application.port.out.LoadCurrentSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

/** 현재 주 일정 조회와 전체 교체 흐름을 조정하는 application service다. */
public final class ScheduleService implements GetCurrentScheduleUseCase, ReplaceMemberScheduleUseCase {

    private final LoadCurrentSchedulePort loadPort;
    private final SaveMemberSchedulePort savePort;
    private final Clock clock;

    public ScheduleService(
            LoadCurrentSchedulePort loadPort,
            SaveMemberSchedulePort savePort,
            Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public CurrentScheduleResult getCurrentSchedule() {
        // 조회 기준 주는 클라이언트 입력이 아니라 서버의 한국 시간으로 일관되게 결정한다.
        WeekPeriod week = currentWeek();
        LoadCurrentSchedulePort.CurrentScheduleData data = loadPort.loadCurrentSchedule(week);

        // Persistence 전용 data가 Web까지 전파되지 않도록 application 출력 계약으로 변환한다.
        return new CurrentScheduleResult(
                week.start(),
                week.end(),
                data.members().stream().map(member -> new CurrentScheduleResult.Member(
                        member.id(), member.name(), member.server(), member.position(),
                        member.submitted(), member.updatedAt())).toList(),
                data.availability().stream().map(availability -> new CurrentScheduleResult.Availability(
                        availability.memberId(), availability.date(), availability.slots())).toList());
    }

    @Override
    public ReplaceMemberScheduleResult replaceMemberSchedule(ReplaceMemberScheduleCommand command) {
        WeekPeriod week = currentWeek();
        // 화면을 오래 열어 둔 요청이 다른 주의 현재 일정을 덮어쓰지 못하게 한다.
        if (!week.start().equals(command.weekStart())) {
            throw new CurrentWeekMismatchException();
        }

        // 중복 slot은 복합 기본키 충돌 전에 제거하되 사용자가 보낸 순서는 유지한다.
        List<ScheduleSlot> slots = List.copyOf(new LinkedHashSet<>(command.slots()));
        slots.forEach(slot -> slot.requireWithin(week));

        // 모든 변경 행이 동일한 수정 시각을 갖도록 transaction 호출 전에 한 번만 계산한다.
        LocalDateTime updatedAt = LocalDateTime.now(clock);
        SaveMemberSchedulePort.SavedScheduleData saved = savePort.replaceSchedule(
                        command.memberId(), week, slots, updatedAt)
                .orElseThrow(MemberNotFoundException::new);
        return new ReplaceMemberScheduleResult(
                command.memberId(), week.start(), saved.revision(), saved.updatedAt());
    }

    private WeekPeriod currentWeek() {
        return WeekPeriod.containing(LocalDate.now(clock));
    }
}
