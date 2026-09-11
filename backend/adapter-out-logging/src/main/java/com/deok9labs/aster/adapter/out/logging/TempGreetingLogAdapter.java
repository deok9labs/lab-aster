package com.deok9labs.aster.adapter.out.logging;

import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.domain.model.TempGreeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempGreetingLogAdapter implements TempGreetingLogPort {

    private static final Logger log = LoggerFactory.getLogger(TempGreetingLogAdapter.class);

    @Override
    public void log(TempGreeting greeting) {
        log.info("{}", greeting.message());
    }
}
