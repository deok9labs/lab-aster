package com.deok9labs.aster.adapter.in.web;

import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.domain.model.TempGreeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 임시 인사말 유스케이스를 HTTP로 노출하는 inbound adapter다.
 *
 * <p>실제 유스케이스가 도입되면 해당 요청 계약을 표현하는 adapter로 교체하거나 제거한다.</p>
 */
@RestController
@RequestMapping("/hello")
public class TempHelloController {

    private final TempHelloUseCase helloUseCase;

    /**
     * 인사말 유스케이스를 사용하는 controller를 생성한다.
     *
     * @param helloUseCase HTTP 요청을 전달할 application 경계
     */
    public TempHelloController(TempHelloUseCase helloUseCase) {
        this.helloUseCase = helloUseCase;
    }

    /**
     * 임시 인사말을 생성한다.
     *
     * @return 유스케이스가 생성한 인사말 응답
     */
    @GetMapping
    public TempHelloResponse hello() {
        TempGreeting greeting = helloUseCase.sayHello();
        return new TempHelloResponse(greeting.message());
    }

    /**
     * HTTP 응답으로 전달할 임시 인사말이다.
     *
     * @param message 사용자에게 표시할 인사말
     */
    public record TempHelloResponse(String message) {
    }
}
