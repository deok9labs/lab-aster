package com.deok9labs.aster.configuration;

import com.deok9labs.aster.adapter.out.logging.TempGreetingLogAdapter;
import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.application.service.TempHelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 임시 인사말 흐름의 port와 adapter를 조립하는 bootstrap 설정이다.
 *
 * <p>실제 유스케이스 구성이 도입되면 해당 설정으로 교체하거나 제거한다.</p>
 */
@Configuration
public class TempHelloConfig {

    /**
     * Spring이 인사말 구성 정보를 등록할 설정 객체를 생성한다.
     */
    public TempHelloConfig() {
    }

    @Bean
    TempGreetingLogPort tempGreetingLogPort() {
        return new TempGreetingLogAdapter();
    }

    @Bean
    TempHelloUseCase tempHelloUseCase(TempGreetingLogPort greetingLogPort) {
        return new TempHelloService(greetingLogPort);
    }
}
