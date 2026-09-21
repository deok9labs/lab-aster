package com.deok9labs.aster.ember.domain;

import java.time.LocalDate;

/**
 * 특정 날짜에 팀원이 선택한 18:00부터 24:00까지의 30분 단위 시각이다.
 *
 * @param date 가능한 날짜
 * @param slotTime 날짜를 넘기지 않고 선택한 화면상의 시각
 */
public record ScheduleSlot(LocalDate date, ScheduleTime slotTime) {

    public ScheduleSlot {
        if (date == null || slotTime == null) {
            throw new IllegalArgumentException("일정 날짜와 시간이 필요합니다.");
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
