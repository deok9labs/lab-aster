package com.deok9labs.aster.ember.application.service;

/** 요청한 활성 팀원이 존재하지 않을 때 발생한다. */
public final class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("팀원을 찾을 수 없습니다.");
    }
}
