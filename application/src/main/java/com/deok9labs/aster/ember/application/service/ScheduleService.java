package com.deok9labs.aster.ember.application.service;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.ScheduleWeek;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import com.deok9labs.aster.ember.application.port.in.GetScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
import com.deok9labs.aster.ember.application.port.in.result.ScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
import com.deok9labs.aster.ember.application.port.out.LoadSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

/** 이번 주와 다음 주 일정 조회 및 전체 교체 흐름을 조정하는 application service다. */
public final class ScheduleService implements GetScheduleUseCase, ReplaceMemberScheduleUseCase {

    private final LoadSchedulePort loadPort;
    private final SaveMemberSchedulePort savePort;
    private final Clock clock;

    public ScheduleService(
            LoadSchedulePort loadPort,
            SaveMemberSchedulePort savePort,
            Clock clock) {
        this.loadPort = loadPort;
        this.savePort = savePort;
        this.clock = clock;
    }

    @Override
    public ScheduleResult getSchedule(ScheduleWeek scheduleWeek) {
        WeekPeriod week = resolveWeek(scheduleWeek);
        LoadSchedulePort.ScheduleData data = loadPort.loadSchedule(week);

        // Persistence 전용 data가 Web까지 전파되지 않도록 application 출력 계약으로 변환한다.
        return new ScheduleResult(
                week.start(),
                week.end(),
                data.members().stream().map(member -> new ScheduleResult.Member(
                        member.id(), member.name(), member.server(), member.position(),
                        member.submitted(), member.updatedAt())).toList(),
                data.availability().stream()
                        .map(availability -> new ScheduleResult.Availability(
                                availability.memberId(),
                                availability.date(),
                                availability.slots()))
                        .toList());
    }

    @Override
    public ReplaceMemberScheduleResult replaceMemberSchedule(ReplaceMemberScheduleCommand command) {
        WeekPeriod week = resolveWeek(command.scheduleWeek());
        // 경로가 주차를 결정하고 expected 값은 화면을 오래 열어 둔 요청의 주차 이동만 탐지한다.
        if (!week.start().equals(command.expectedWeekStart())) {
            throw new ScheduleWeekMismatchException();
        }

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

    private WeekPeriod resolveWeek(ScheduleWeek scheduleWeek) {
        // 주차는 임의 날짜 입력이 아니라 서버의 한국 시간과 제한된 상대 주차로 결정한다.
        return scheduleWeek.resolve(LocalDate.now(clock));
    }

}
