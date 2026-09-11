package com.deok9labs.aster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aster backend의 Spring Boot 실행 진입점이다.
 */
@SpringBootApplication
public class AsterApplication {

    /**
     * Spring Boot가 사용할 application 진입점 객체를 생성한다.
     */
    public AsterApplication() {
    }

    /**
     * Spring application context를 시작한다.
     *
     * @param args application에 전달된 명령행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(AsterApplication.class, args);
    }
}
