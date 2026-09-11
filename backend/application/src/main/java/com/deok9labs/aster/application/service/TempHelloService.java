package com.deok9labs.aster.application.service;

import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.domain.model.TempGreeting;

public final class TempHelloService implements TempHelloUseCase {

    private final TempGreetingLogPort greetingLogPort;

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
