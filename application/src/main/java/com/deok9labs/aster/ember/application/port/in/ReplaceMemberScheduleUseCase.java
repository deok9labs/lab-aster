package com.deok9labs.aster.ember.application.port.in;

import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;

/** 활성 팀원의 현재 주 가능 시간을 전체 교체하는 경계다. */
public interface ReplaceMemberScheduleUseCase {

    ReplaceMemberScheduleResult replaceMemberSchedule(ReplaceMemberScheduleCommand command);
}
