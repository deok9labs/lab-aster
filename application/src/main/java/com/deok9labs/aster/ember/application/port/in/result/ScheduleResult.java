package com.deok9labs.aster.ember.application.port.in.result;

import com.deok9labs.aster.ember.domain.ScheduleTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 선택한 이번 주 또는 다음 주 일정 조회 유스케이스의 출력 계약이다. */
public record ScheduleResult(
        LocalDate weekStart,
        LocalDate weekEnd,
        List<Member> members,
        List<Availability> availability) {

    public ScheduleResult {
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

    public record Availability(int memberId, LocalDate date, List<ScheduleTime> slots) {

        public Availability {
            slots = List.copyOf(slots);
        }
    }
}
