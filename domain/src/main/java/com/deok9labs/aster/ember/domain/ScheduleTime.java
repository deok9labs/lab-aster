package com.deok9labs.aster.ember.domain;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 일정 화면에서 사용하는 18:00부터 24:00까지의 30분 단위 시각이다.
 *
 * <p>{@link LocalTime}이 표현하지 못하는 하루 끝 {@code 24:00}을 포함하기 위해
 * 자정부터의 분으로 보존한다.</p>
 */
public record ScheduleTime(int minuteOfDay) implements Comparable<ScheduleTime> {

    public static final int FIRST_MINUTE = 18 * 60;
    public static final int LAST_MINUTE = 24 * 60;
    private static final Pattern FORMAT = Pattern.compile("^(\\d{2}):(\\d{2})$");

    public ScheduleTime {
        if (minuteOfDay < FIRST_MINUTE
                || minuteOfDay > LAST_MINUTE
                || minuteOfDay % 30 != 0) {
            throw new IllegalArgumentException("일정 시간은 18:00부터 24:00까지 30분 단위여야 합니다.");
        }
    }

    /** {@code HH:mm} 문자열을 변환하며 하루 끝의 {@code 24:00}도 허용한다. */
    public static ScheduleTime parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("일정 시간이 필요합니다.");
        }
        Matcher matcher = FORMAT.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("일정 시간 형식은 HH:mm이어야 합니다.");
        }
        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        if (minute >= 60) {
            throw new IllegalArgumentException("일정 시간 형식은 HH:mm이어야 합니다.");
        }
        return new ScheduleTime((hour * 60) + minute);
    }

    /** DB 슬롯으로 저장 가능한 24:00 미만의 시각으로 변환한다. */
    public LocalTime toSlotTime() {
        if (minuteOfDay == LAST_MINUTE) {
            throw new IllegalStateException("24:00은 일정 범위의 종료 시각으로만 사용할 수 있습니다.");
        }
        return LocalTime.of(minuteOfDay / 60, minuteOfDay % 60);
    }

    @Override
    public int compareTo(ScheduleTime other) {
        return Integer.compare(minuteOfDay, other.minuteOfDay);
    }

    @Override
    public String toString() {
        return "%02d:%02d".formatted(minuteOfDay / 60, minuteOfDay % 60);
    }
}
