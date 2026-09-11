package com.deok9labs.aster.domain.model;

import java.util.Objects;

public record TempGreeting(String message) {

    public TempGreeting {
        Objects.requireNonNull(message, "message must not be null");
    }
}
