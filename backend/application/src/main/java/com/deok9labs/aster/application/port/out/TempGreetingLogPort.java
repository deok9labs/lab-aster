package com.deok9labs.aster.application.port.out;

import com.deok9labs.aster.domain.model.TempGreeting;

/**
 * application이 생성한 인사말의 외부 기록을 요청하는 outbound port다.
 */
@FunctionalInterface
public interface TempGreetingLogPort {

    /**
     * 인사말을 외부 기록 대상으로 전달한다.
     *
     * @param greeting 기록할 인사말
     */
    void log(TempGreeting greeting);
}
