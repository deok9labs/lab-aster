package com.deok9labs.aster.domain.model;

import java.util.Objects;

/**
 * 변환 규칙을 적용한 결과다.
 *
 * @param number 입력 정수의 2배. {@code int} 범위를 넘을 수 있어 {@code long}으로 표현한다
 * @param text 입력 문자열의 글자 순서를 뒤집은 문자열
 */
public record TransformationResult(long number, String text) {

    /**
     * 문자열이 없는 결과는 허용하지 않는다.
     *
     * @throws NullPointerException text가 {@code null}일 때
     */
    public TransformationResult {
        Objects.requireNonNull(text, "text must not be null");
    }
}
