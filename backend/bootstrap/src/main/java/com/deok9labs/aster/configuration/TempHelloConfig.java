package com.deok9labs.aster.configuration;

import com.deok9labs.aster.adapter.out.logging.TempGreetingLogAdapter;
import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.application.service.TempHelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempHelloConfig {

    @Bean
    TempGreetingLogPort tempGreetingLogPort() {
        return new TempGreetingLogAdapter();
    }

    @Bean
    TempHelloUseCase tempHelloUseCase(TempGreetingLogPort greetingLogPort) {
        return new TempHelloService(greetingLogPort);
    }
}
