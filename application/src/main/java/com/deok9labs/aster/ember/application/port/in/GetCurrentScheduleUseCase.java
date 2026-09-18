package com.deok9labs.aster.ember.application.port.in;

import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;

/** 한국 시간 기준 현재 주의 활성 팀원과 가능 시간을 조회하는 경계다. */
public interface GetCurrentScheduleUseCase {

    CurrentScheduleResult getCurrentSchedule();
}
