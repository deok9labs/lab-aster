package com.deok9labs.aster.configuration;

import com.deok9labs.aster.application.port.in.TransformationUseCase;
import com.deok9labs.aster.application.service.TransformationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 변환 유스케이스를 조립하는 bootstrap 설정이다.
 *
 * <p>application 모듈이 Spring에 의존하지 않도록 service는 component scan 대신 이 설정에서 bean으로 등록한다.</p>
 */
@Configuration
public class TransformationConfig {

    @Bean
    TransformationUseCase transformationUseCase() {
        return new TransformationService();
    }
}
