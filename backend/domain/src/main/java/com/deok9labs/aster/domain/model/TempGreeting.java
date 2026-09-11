package com.deok9labs.aster.domain.model;

import java.util.Objects;

/**
 * 사용자에게 전달할 임시 인사말이다.
 *
 * <p>인사말이 없는 상태는 허용하지 않으므로 {@code message}는 {@code null}일 수 없다.</p>
 *
 * @param message 사용자에게 표시할 인사말
 */
public record TempGreeting(String message) {

    /**
     * 인사말의 null 불변식을 검증한다.
     *
     * @throws NullPointerException message가 {@code null}일 때
     */
    public TempGreeting {
        Objects.requireNonNull(message, "message must not be null");
    }
}
