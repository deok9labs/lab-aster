package com.deok9labs.aster.adapter.in.web;

import com.deok9labs.aster.application.port.in.TempHelloUseCase;
import com.deok9labs.aster.domain.model.TempGreeting;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class TempHelloController {

    private final TempHelloUseCase helloUseCase;

    public TempHelloController(TempHelloUseCase helloUseCase) {
        this.helloUseCase = helloUseCase;
    }

    @GetMapping
    public TempHelloResponse hello() {
        TempGreeting greeting = helloUseCase.sayHello();
        return new TempHelloResponse(greeting.message());
    }

    public record TempHelloResponse(String message) {
    }
}
