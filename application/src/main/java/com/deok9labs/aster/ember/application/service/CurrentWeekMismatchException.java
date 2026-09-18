package com.deok9labs.aster.ember.application.service;

/** 클라이언트가 편집한 주와 서버의 현재 주가 다를 때 발생한다. */
public final class CurrentWeekMismatchException extends RuntimeException {

    public CurrentWeekMismatchException() {
        super("현재 주 일정만 저장할 수 있습니다.");
    }
}
