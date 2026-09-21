package com.deok9labs.aster.ember.domain;

import java.time.LocalDate;
import java.util.Locale;

/** 서버 기준일로부터 조회하거나 수정할 상대 주차다. */
public enum ScheduleWeek {
    CURRENT(0, "current"),
    NEXT(1, "next");

    private final int weekOffset;
    private final String pathValue;

    ScheduleWeek(int weekOffset, String pathValue) {
        this.weekOffset = weekOffset;
        this.pathValue = pathValue;
    }

    /**
     * 서버 기준일을 기준으로 이 주차의 월요일부터 일요일까지를 계산한다.
     *
     * @param referenceDate 서비스 시간대가 적용된 기준일
     * @return 선택한 상대 주차의 기간
     */
    public WeekPeriod resolve(LocalDate referenceDate) {
        WeekPeriod current = WeekPeriod.containing(referenceDate);
        LocalDate start = current.start().plusWeeks(weekOffset);
        return new WeekPeriod(start, start.plusDays(6));
    }

    /** HTTP 경로에서 사용하는 소문자 주차 값을 변환한다. */
    public static ScheduleWeek fromPath(String value) {
        if (value == null) {
            throw new IllegalArgumentException("일정 주차가 필요합니다.");
        }
        String normalized = value.toLowerCase(Locale.ROOT);
        for (ScheduleWeek week : values()) {
            if (week.pathValue.equals(normalized)) {
                return week;
            }
        }
        throw new IllegalArgumentException("일정 주차는 current 또는 next여야 합니다.");
    }
}
