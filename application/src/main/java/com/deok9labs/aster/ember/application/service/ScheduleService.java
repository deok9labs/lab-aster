package com.deok9labs.aster.ember.application.service;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.ScheduleWeek;
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
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/** 이번 주와 다음 주 일정 조회 및 전체 교체 흐름을 조정하는 application service다. */
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
    public CurrentScheduleResult getSchedule(ScheduleWeek scheduleWeek) {
        WeekPeriod week = resolveWeek(scheduleWeek);
        LoadCurrentSchedulePort.CurrentScheduleData data = loadPort.loadCurrentSchedule(week);

        // Persistence 전용 data가 Web까지 전파되지 않도록 application 출력 계약으로 변환한다.
        return new CurrentScheduleResult(
                week.start(),
                week.end(),
                data.members().stream().map(member -> new CurrentScheduleResult.Member(
                        member.id(), member.name(), member.server(), member.position(),
                        member.submitted(), member.updatedAt())).toList(),
                data.availability().stream()
                        .map(availability -> new CurrentScheduleResult.Availability(
                                availability.memberId(),
                                availability.date(),
                                toRanges(availability.slots())))
                        .filter(availability -> !availability.ranges().isEmpty())
                        .toList());
    }

    @Override
    public ReplaceMemberScheduleResult replaceMemberSchedule(ReplaceMemberScheduleCommand command) {
        WeekPeriod week = resolveWeek(command.scheduleWeek());
        // 경로가 주차를 결정하고 expected 값은 화면을 오래 열어 둔 요청의 주차 이동만 탐지한다.
        if (!week.start().equals(command.expectedWeekStart())) {
            throw new ScheduleWeekMismatchException();
        }

        List<ScheduleSlot> slots = List.copyOf(new LinkedHashSet<>(
                command.ranges().stream().flatMap(range -> range.toSlots().stream()).toList()));
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

    private List<CurrentScheduleResult.TimeRange> toRanges(List<LocalTime> storedSlots) {
        List<Integer> minutes = storedSlots.stream()
                // 이전 계약에서 저장된 다른 시간대가 있어도 새 화면의 조회 전체를 실패시키지 않는다.
                .filter(time -> !time.isBefore(LocalTime.of(18, 0)))
                .map(time -> (time.getHour() * 60) + time.getMinute())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();
        if (minutes.isEmpty()) {
            return List.of();
        }

        java.util.ArrayList<CurrentScheduleResult.TimeRange> ranges = new java.util.ArrayList<>();
        int start = minutes.getFirst();
        int previous = start;
        for (int index = 1; index < minutes.size(); index++) {
            int current = minutes.get(index);
            if (current != previous + 30) {
                ranges.add(timeRange(start, previous + 30));
                start = current;
            }
            previous = current;
        }
        ranges.add(timeRange(start, previous + 30));
        return List.copyOf(ranges);
    }

    private CurrentScheduleResult.TimeRange timeRange(int startMinute, int endMinute) {
        return new CurrentScheduleResult.TimeRange(
                new ScheduleTime(startMinute),
                new ScheduleTime(endMinute));
    }
}
