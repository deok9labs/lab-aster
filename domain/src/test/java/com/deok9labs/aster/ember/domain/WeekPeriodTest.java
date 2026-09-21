package com.deok9labs.aster.ember.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class WeekPeriodTest {

    @Test
    void createsMondayToSundayPeriodContainingDate() {
        WeekPeriod week = WeekPeriod.containing(LocalDate.of(2026, 9, 18));

        assertEquals(LocalDate.of(2026, 9, 14), week.start());
        assertEquals(LocalDate.of(2026, 9, 20), week.end());
    }

    @Test
    void rejectsSlotOutsideWeek() {
        WeekPeriod week = WeekPeriod.containing(LocalDate.of(2026, 9, 18));
        ScheduleSlot slot = new ScheduleSlot(
                LocalDate.of(2026, 9, 21), LocalTime.of(19, 0));

        assertThrows(IllegalArgumentException.class, () -> slot.requireWithin(week));
    }

    @Test
    void rejectsTimeOutsideThirtyMinuteGrid() {
        assertThrows(IllegalArgumentException.class, () -> new ScheduleSlot(
                LocalDate.of(2026, 9, 18), LocalTime.of(19, 15)));
    }

    @Test
    void expandsRangeEndingAtMidnightIntoPersistableSlots() {
        AvailabilityRange range = new AvailabilityRange(
                LocalDate.of(2026, 9, 18),
                ScheduleTime.parse("21:30"),
                ScheduleTime.parse("24:00"));

        assertEquals(List.of(
                LocalTime.of(21, 30),
                LocalTime.of(22, 0),
                LocalTime.of(22, 30),
                LocalTime.of(23, 0),
                LocalTime.of(23, 30)), range.toSlots().stream().map(ScheduleSlot::time).toList());
    }
}
