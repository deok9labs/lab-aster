package com.deok9labs.aster.application.service;

import com.deok9labs.aster.application.port.in.TransformationUseCase;
import com.deok9labs.aster.domain.model.TransformationResult;
import com.deok9labs.aster.domain.model.TransformationSource;

/**
 * 변환 규칙을 domain에 위임하는 application service다.
 *
 * <p>변환은 외부 시스템이나 저장된 상태를 사용하지 않으므로 outbound port와 transaction 경계를 두지 않는다.</p>
 */
public final class TransformationService implements TransformationUseCase {

    @Override
    public TransformationResult transform(TransformationSource source) {
        return source.transform();
    }
}
