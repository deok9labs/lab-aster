package com.deok9labs.aster.ember.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
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
                LocalDate.of(2026, 9, 21), ScheduleTime.parse("19:00"));

        assertThrows(IllegalArgumentException.class, () -> slot.requireWithin(week));
    }

    @Test
    void rejectsTimeOutsideThirtyMinuteGrid() {
        assertThrows(IllegalArgumentException.class, () -> ScheduleTime.parse("19:15"));
    }

    @Test
    void keepsMidnightAsSameDateSlot() {
        ScheduleSlot slot = new ScheduleSlot(
                LocalDate.of(2026, 9, 18), ScheduleTime.parse("24:00"));

        assertEquals(LocalDate.of(2026, 9, 18), slot.date());
        assertEquals("24:00", slot.slotTime().toString());
    }
}
