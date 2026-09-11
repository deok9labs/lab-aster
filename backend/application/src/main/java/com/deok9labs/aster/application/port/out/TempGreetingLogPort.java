package com.deok9labs.aster.application.port.out;

import com.deok9labs.aster.domain.model.TempGreeting;

@FunctionalInterface
public interface TempGreetingLogPort {

    void log(TempGreeting greeting);
}
