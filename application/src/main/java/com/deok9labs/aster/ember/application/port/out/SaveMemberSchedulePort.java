package com.deok9labs.aster.ember.application.port.out;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** 팀원 일정 전체 교체를 database transaction 하나로 수행하는 outbound 경계다. */
public interface SaveMemberSchedulePort {

    Optional<SavedScheduleData> replaceSchedule(
            int memberId,
            WeekPeriod week,
            List<ScheduleSlot> slots,
            LocalDateTime updatedAt);

    record SavedScheduleData(int revision, LocalDateTime updatedAt) {
    }
}
