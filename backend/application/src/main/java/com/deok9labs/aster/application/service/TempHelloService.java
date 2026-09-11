package com.deok9labs.aster.application.service;

import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.domain.model.TempGreeting;

/**
 * 임시 인사말을 생성하고 외부 기록 경계로 전달하는 application service다.
 *
 * <p>실제 business use case가 도입되면 해당 흐름을 구현하는 service로 교체하거나 제거한다.</p>
 */
public final class TempHelloService implements TempHelloUseCase {

    private final TempGreetingLogPort greetingLogPort;

    /**
     * 인사말 기록 경계를 사용하는 service를 생성한다.
     *
     * @param greetingLogPort 생성된 인사말을 전달할 외부 기록 경계
     */
    public TempHelloService(TempGreetingLogPort greetingLogPort) {
        this.greetingLogPort = greetingLogPort;
    }

    @Override
    public TempGreeting sayHello() {
        TempGreeting greeting = new TempGreeting("Hello World");
        greetingLogPort.log(greeting);
        return greeting;
    }
}
