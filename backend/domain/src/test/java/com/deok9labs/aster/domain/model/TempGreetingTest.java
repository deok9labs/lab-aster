package com.deok9labs.aster.domain.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TempGreetingTest {

    @Test
    void rejectsNullMessage() {
        assertThrows(NullPointerException.class, () -> new TempGreeting(null));
    }
}
