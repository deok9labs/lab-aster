package com.deok9labs.aster.ember.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
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
    void replacesCurrentWeekScheduleAfterRemovingDuplicates() {
        CapturingSavePort savePort = new CapturingSavePort();
        ScheduleService service = new ScheduleService(EMPTY_LOAD_PORT, savePort, CLOCK);
        ScheduleSlot slot = new ScheduleSlot(LocalDate.of(2026, 9, 18), LocalTime.of(19, 0));

        ReplaceMemberScheduleResult result = service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(
                        1, LocalDate.of(2026, 9, 14), List.of(slot, slot)));

        assertEquals(1, savePort.slots.size());
        assertEquals(2, result.revision());
        assertEquals(LocalDateTime.of(2026, 9, 18, 21, 0), result.updatedAt());
    }

    @Test
    void rejectsWeekDifferentFromServerCurrentWeek() {
        ScheduleService service = new ScheduleService(
                EMPTY_LOAD_PORT, new CapturingSavePort(), CLOCK);

        assertThrows(CurrentWeekMismatchException.class, () -> service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(1, LocalDate.of(2026, 9, 7), List.of())));
    }

    @Test
    void reportsMissingActiveMember() {
        SaveMemberSchedulePort missingMemberPort =
                (memberId, week, slots, updatedAt) -> Optional.empty();
        ScheduleService service = new ScheduleService(EMPTY_LOAD_PORT, missingMemberPort, CLOCK);

        assertThrows(MemberNotFoundException.class, () -> service.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(1, LocalDate.of(2026, 9, 14), List.of())));
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
