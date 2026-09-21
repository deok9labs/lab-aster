package com.deok9labs.aster.ember.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.AvailabilityRange;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.ScheduleWeek;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
import com.deok9labs.aster.ember.application.port.out.LoadCurrentSchedulePort;
import com.deok9labs.aster.ember.application.port.out.SaveMemberSchedulePort;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScheduleServiceTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-18T12:00:00Z"), ZoneId.of("Asia/Seoul"));
    private static final LoadCurrentSchedulePort EMPTY_LOAD_PORT = week ->
            new LoadCurrentSchedulePort.CurrentScheduleData(List.of(), List.of());

    @Test
    void replacesNextWeekRangesAfterExpandingAndRemovingDuplicateSlots() {
        CapturingSavePort savePort = new CapturingSavePort();
        ScheduleService service = new ScheduleService(EMPTY_LOAD_PORT, savePort, CLOCK);
        AvailabilityRange first = new AvailabilityRange(
                LocalDate.of(2026, 9, 21), ScheduleTime.parse("18:00"), ScheduleTime.parse("19:00"));
        AvailabilityRange second = new AvailabilityRange(
                LocalDate.of(2026, 9, 21), ScheduleTime.parse("18:30"), ScheduleTime.parse("19:30"));

        ReplaceMemberScheduleResult result = service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(
                        1,
                        ScheduleWeek.NEXT,
                        LocalDate.of(2026, 9, 21),
                        List.of(first, second)));

        assertEquals(List.of(
                new ScheduleSlot(LocalDate.of(2026, 9, 21), LocalTime.of(18, 0)),
                new ScheduleSlot(LocalDate.of(2026, 9, 21), LocalTime.of(18, 30)),
                new ScheduleSlot(LocalDate.of(2026, 9, 21), LocalTime.of(19, 0))), savePort.slots);
        assertEquals(2, result.revision());
        assertEquals(LocalDateTime.of(2026, 9, 18, 21, 0), result.updatedAt());
    }

    @Test
    void restoresSeparateRangesAndMidnightEndFromStoredSlots() {
        LoadCurrentSchedulePort loadPort = week -> new LoadCurrentSchedulePort.CurrentScheduleData(
                List.of(),
                List.of(new LoadCurrentSchedulePort.AvailabilityData(
                        1,
                        LocalDate.of(2026, 9, 18),
                        List.of(
                                LocalTime.of(18, 0),
                                LocalTime.of(18, 30),
                                LocalTime.of(21, 30),
                                LocalTime.of(22, 0),
                                LocalTime.of(22, 30),
                                LocalTime.of(23, 0),
                                LocalTime.of(23, 30)))));
        ScheduleService service = new ScheduleService(loadPort, new CapturingSavePort(), CLOCK);

        var result = service.getSchedule(ScheduleWeek.CURRENT);

        assertEquals(List.of(
                new CurrentScheduleResult.TimeRange(
                        ScheduleTime.parse("18:00"), ScheduleTime.parse("19:00")),
                new CurrentScheduleResult.TimeRange(
                        ScheduleTime.parse("21:30"), ScheduleTime.parse("24:00"))),
                result.availability().getFirst().ranges());
    }

    @Test
    void rejectsExpectedWeekDifferentFromSelectedServerWeek() {
        ScheduleService service = new ScheduleService(
                EMPTY_LOAD_PORT, new CapturingSavePort(), CLOCK);

        assertThrows(ScheduleWeekMismatchException.class, () -> service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(
                        1, ScheduleWeek.NEXT, LocalDate.of(2026, 9, 14), List.of())));
    }

    @Test
    void reportsMissingActiveMember() {
        SaveMemberSchedulePort missingMemberPort =
                (memberId, week, slots, updatedAt) -> Optional.empty();
        ScheduleService service = new ScheduleService(EMPTY_LOAD_PORT, missingMemberPort, CLOCK);

        assertThrows(MemberNotFoundException.class, () -> service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(
                        1, ScheduleWeek.CURRENT, LocalDate.of(2026, 9, 14), List.of())));
    }

    private static final class CapturingSavePort implements SaveMemberSchedulePort {

        private List<ScheduleSlot> slots;

        @Override
        public Optional<SavedScheduleData> replaceSchedule(
                int memberId,
                WeekPeriod week,
                List<ScheduleSlot> slots,
                LocalDateTime updatedAt) {
            this.slots = slots;
            return Optional.of(new SavedScheduleData(2, updatedAt));
        }
    }
}
