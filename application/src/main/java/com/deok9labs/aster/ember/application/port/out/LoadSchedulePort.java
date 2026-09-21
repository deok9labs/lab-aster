package com.deok9labs.aster.ember.application.port.out;

import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 선택한 이번 주 또는 다음 주 화면에 필요한 persistence data를 조회하는 outbound 경계다. */
public interface LoadSchedulePort {

    ScheduleData loadSchedule(WeekPeriod week);

    record ScheduleData(List<MemberData> members, List<AvailabilityData> availability) {

        public ScheduleData {
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

    record AvailabilityData(int memberId, LocalDate date, List<ScheduleTime> slots) {

        public AvailabilityData {
            slots = List.copyOf(slots);
        }
    }
}
