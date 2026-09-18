package com.deok9labs.aster.ember.application.port.in.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** 현재 주 일정 조회 유스케이스의 출력 계약이다. */
public record CurrentScheduleResult(
        LocalDate weekStart,
        LocalDate weekEnd,
        List<Member> members,
        List<Availability> availability) {

    public CurrentScheduleResult {
        members = List.copyOf(members);
        availability = List.copyOf(availability);
    }

    public record Member(
            int id,
            String name,
            String server,
            String position,
            boolean submitted,
            LocalDateTime updatedAt) {
    }

    public record Availability(int memberId, LocalDate date, List<LocalTime> slots) {

        public Availability {
            slots = List.copyOf(slots);
        }
    }
}
