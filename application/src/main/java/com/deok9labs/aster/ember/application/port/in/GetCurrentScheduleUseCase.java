package com.deok9labs.aster.ember.application.port.in;

import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;
import com.deok9labs.aster.ember.domain.ScheduleWeek;

/** 한국 시간 기준 이번 주 또는 다음 주의 활성 팀원과 가능 시간을 조회하는 경계다. */
public interface GetCurrentScheduleUseCase {

    CurrentScheduleResult getSchedule(ScheduleWeek scheduleWeek);
}
