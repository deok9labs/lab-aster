package com.deok9labs.aster.ember.application.port.in.result;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 일정 교체가 완료된 제출 상태다. */
public record ReplaceMemberScheduleResult(
        int memberId,
        LocalDate weekStart,
        int revision,
        LocalDateTime updatedAt) {
}
