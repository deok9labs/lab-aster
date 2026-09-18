package com.deok9labs.aster.ember.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 월요일부터 일요일까지 이어지는 한 주다.
 *
 * @param start 주의 월요일
 * @param end 주의 일요일
 */
public record WeekPeriod(LocalDate start, LocalDate end) {

    public WeekPeriod {
        if (start == null || end == null) {
            throw new IllegalArgumentException("주간 범위의 날짜가 필요합니다.");
        }
        if (start.getDayOfWeek() != DayOfWeek.MONDAY || !end.equals(start.plusDays(6))) {
            throw new IllegalArgumentException("주간 범위는 월요일부터 일요일까지여야 합니다.");
        }
    }

    /**
     * 주어진 날짜가 속한 월요일부터 일요일까지의 범위를 만든다.
     *
     * @param date 기준 날짜
     * @return 기준 날짜가 속한 주
     */
    public static WeekPeriod containing(LocalDate date) {
        LocalDate start = date.minusDays(date.getDayOfWeek().getValue() - 1L);
        return new WeekPeriod(start, start.plusDays(6));
    }

    public boolean contains(LocalDate date) {
        return date != null && !date.isBefore(start) && !date.isAfter(end);
    }
}
