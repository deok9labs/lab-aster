package com.deok9labs.aster.ember.application.port.in.command;

import com.deok9labs.aster.ember.domain.AvailabilityRange;
import com.deok9labs.aster.ember.domain.ScheduleWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * 팀원 한 명의 한 주 일정을 전체 교체하는 application 입력이다.
 *
 * @param memberId 대상 팀원 식별자
 * @param scheduleWeek 서버 기준으로 선택할 이번 주 또는 다음 주
 * @param expectedWeekStart 클라이언트가 편집을 시작한 주의 월요일
 * @param ranges 교체 후 남길 날짜별 가용 시간 범위
 */
public record ReplaceMemberScheduleCommand(
        int memberId,
        ScheduleWeek scheduleWeek,
        LocalDate expectedWeekStart,
        List<AvailabilityRange> ranges) {

    public ReplaceMemberScheduleCommand {
        if (memberId <= 0 || scheduleWeek == null || expectedWeekStart == null || ranges == null) {
            throw new IllegalArgumentException("일정 교체 요청 값이 올바르지 않습니다.");
        }
        ranges = List.copyOf(ranges);
    }
}
