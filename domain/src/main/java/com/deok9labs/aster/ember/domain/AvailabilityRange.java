package com.deok9labs.aster.ember.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** 한 날짜 안에서 시작 시각 이상, 종료 시각 미만으로 이어지는 가용 시간 범위다. */
public record AvailabilityRange(
        LocalDate date,
        ScheduleTime startTime,
        ScheduleTime endTime) {

    public AvailabilityRange {
        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("가용 시간 범위의 날짜와 시간이 필요합니다.");
        }
        if (startTime.compareTo(endTime) >= 0) {
            throw new IllegalArgumentException("가용 시간 종료 시각은 시작 시각보다 늦어야 합니다.");
        }
    }

    /** 범위를 DB가 보존하는 30분 단위 시작 슬롯으로 펼친다. */
    public List<ScheduleSlot> toSlots() {
        List<ScheduleSlot> slots = new ArrayList<>();
        for (int minute = startTime.minuteOfDay(); minute < endTime.minuteOfDay(); minute += 30) {
            slots.add(new ScheduleSlot(date, new ScheduleTime(minute).toSlotTime()));
        }
        return List.copyOf(slots);
    }
}
