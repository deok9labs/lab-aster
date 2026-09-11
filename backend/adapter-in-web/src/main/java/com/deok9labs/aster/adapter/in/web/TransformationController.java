package com.deok9labs.aster.adapter.in.web;

import com.deok9labs.aster.application.port.in.TransformationUseCase;
import com.deok9labs.aster.domain.model.TransformationResult;
import com.deok9labs.aster.domain.model.TransformationSource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 변환 유스케이스를 REST API로 노출하는 inbound adapter다.
 *
 * <p>변환은 새 resource를 만들지 않으므로 {@code 201 Created}가 아닌 {@code 200 OK}로 결과를 반환한다.
 * 필수 값이 없거나 {@code number}가 {@code int} 범위를 벗어나면 Spring MVC 기본 처리에 따라 {@code 400 Bad Request}를 반환한다.</p>
 */
@RestController
@RequestMapping("/api/v1/transformations")
public class TransformationController {

    private final TransformationUseCase transformationUseCase;

    /**
     * 변환 유스케이스를 사용하는 controller를 생성한다.
     *
     * @param transformationUseCase HTTP 요청을 전달할 application 경계
     */
    public TransformationController(TransformationUseCase transformationUseCase) {
        this.transformationUseCase = transformationUseCase;
    }

    /**
     * 정수는 2배로, 문자열은 뒤집어 반환한다.
     *
     * @param request 변환할 값
     * @return 변환 결과
     */
    @PostMapping
    public TransformationResponse transform(@Valid @RequestBody TransformationRequest request) {
        TransformationResult result = transformationUseCase.transform(
                new TransformationSource(request.number(), request.text()));
        return new TransformationResponse(result.number(), result.text());
    }

    /**
     * 변환 요청 본문이다.
     *
     * <p>누락된 값이 기본값 {@code 0}으로 처리되지 않도록 {@code number}를 wrapper type으로 받고 필수 값으로 검증한다.</p>
     *
     * @param number 2배로 만들 {@code int} 범위의 정수
     * @param text 뒤집을 문자열. 빈 문자열은 허용한다
     */
    public record TransformationRequest(@NotNull Integer number, @NotNull String text) {
    }

    /**
     * 변환 응답 본문이다.
     *
     * @param number 입력 정수의 2배. 절댓값이 최대 2^32이므로 JavaScript number로도 정확히 표현된다
     * @param text 입력 문자열의 글자 순서를 뒤집은 문자열
     */
    public record TransformationResponse(long number, String text) {
    }
}
