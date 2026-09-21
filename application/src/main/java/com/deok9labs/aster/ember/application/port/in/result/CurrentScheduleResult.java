package com.deok9labs.aster.ember.application.port.in.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.deok9labs.aster.ember.domain.ScheduleTime;

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

    public record Availability(int memberId, LocalDate date, List<TimeRange> ranges) {

        public Availability {
            ranges = List.copyOf(ranges);
        }
    }

    /** 조회 응답에서 {@code 24:00} 종료를 손실 없이 표현하는 연속 가용 구간이다. */
    public record TimeRange(ScheduleTime startTime, ScheduleTime endTime) {
    }
}
