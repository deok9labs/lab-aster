package com.deok9labs.aster.ember.application.port.out;

import com.deok9labs.aster.ember.domain.WeekPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/** 현재 주 화면에 필요한 persistence data를 조회하는 outbound 경계다. */
public interface LoadCurrentSchedulePort {

    CurrentScheduleData loadCurrentSchedule(WeekPeriod week);

    record CurrentScheduleData(List<MemberData> members, List<AvailabilityData> availability) {

        public CurrentScheduleData {
            members = List.copyOf(members);
            availability = List.copyOf(availability);
        }
    }

    record MemberData(
            int id,
            String name,
            String server,
            String position,
            boolean submitted,
            LocalDateTime updatedAt) {
    }

    record AvailabilityData(int memberId, LocalDate date, List<LocalTime> slots) {

        public AvailabilityData {
            slots = List.copyOf(slots);
        }
    }
}
