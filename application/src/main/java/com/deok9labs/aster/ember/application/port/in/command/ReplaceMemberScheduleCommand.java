package com.deok9labs.aster.ember.application.port.in.command;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import java.time.LocalDate;
import java.util.List;

/**
 * 팀원 한 명의 한 주 일정을 전체 교체하는 application 입력이다.
 *
 * @param memberId 대상 팀원 식별자
 * @param weekStart 클라이언트가 편집한 주의 월요일
 * @param slots 교체 후 남길 시간 목록
 */
public record ReplaceMemberScheduleCommand(
        int memberId,
        LocalDate weekStart,
        List<ScheduleSlot> slots) {

    public ReplaceMemberScheduleCommand {
        if (memberId <= 0 || weekStart == null || slots == null) {
            throw new IllegalArgumentException("일정 교체 요청 값이 올바르지 않습니다.");
        }
        slots = List.copyOf(slots);
    }
}
