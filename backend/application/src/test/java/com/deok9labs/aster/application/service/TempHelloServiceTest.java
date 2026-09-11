package com.deok9labs.aster.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.domain.model.TempGreeting;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class TempHelloServiceTest {

    @Test
    void logsAndReturnsHelloWorldGreeting() {
        AtomicReference<TempGreeting> loggedGreeting = new AtomicReference<>();
        TempGreetingLogPort logPort = loggedGreeting::set;
        TempHelloService service = new TempHelloService(logPort);

        TempGreeting result = service.sayHello();

        assertEquals("Hello World", result.message());
        assertSame(result, loggedGreeting.get());
    }
}
