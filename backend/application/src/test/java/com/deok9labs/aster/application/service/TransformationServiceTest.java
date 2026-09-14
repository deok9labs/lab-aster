package com.deok9labs.aster.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.deok9labs.aster.domain.model.TransformationResult;
import com.deok9labs.aster.domain.model.TransformationSource;
import org.junit.jupiter.api.Test;

class TransformationServiceTest {

    @Test
    void returnsDomainTransformationResult() {
        TransformationResult result = new TransformationService().transform(new TransformationSource(21, "hello"));

        assertEquals(new TransformationResult(42, "olleh"), result);
    }
}
