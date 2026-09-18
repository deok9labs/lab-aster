package com.deok9labs.aster.ember.domain;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 특정 날짜에 팀원이 선택한 30분 단위의 시작 시각이다.
 *
 * @param date 가능한 날짜
 * @param time 00분 또는 30분에 시작하는 시각
 */
public record ScheduleSlot(LocalDate date, LocalTime time) {

    public ScheduleSlot {
        if (date == null || time == null) {
            throw new IllegalArgumentException("일정 날짜와 시간이 필요합니다.");
        }
        if ((time.getMinute() != 0 && time.getMinute() != 30)
                || time.getSecond() != 0
                || time.getNano() != 0) {
            throw new IllegalArgumentException("일정 시간은 30분 단위여야 합니다.");
        }
    }

    /**
     * slot이 지정한 주에 포함되는지 확인한다.
     *
     * @param week 허용되는 주간 범위
     * @throws IllegalArgumentException 날짜가 주간 범위를 벗어날 때
     */
    public void requireWithin(WeekPeriod week) {
        if (!week.contains(date)) {
            throw new IllegalArgumentException("현재 주에 포함되지 않는 일정입니다.");
        }
    }
}
