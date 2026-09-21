package com.deok9labs.aster.ember.application.service;

/** 클라이언트가 편집을 시작한 주와 서버가 해석한 상대 주차가 다를 때 발생한다. */
public final class ScheduleWeekMismatchException extends RuntimeException {

    public ScheduleWeekMismatchException() {
        super("편집한 일정 주차가 변경되었습니다. 새로고침 후 다시 시도해 주세요.");
    }
}
