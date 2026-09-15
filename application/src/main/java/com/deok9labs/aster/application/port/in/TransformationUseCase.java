package com.deok9labs.aster.application.port.in;

import com.deok9labs.aster.domain.model.TransformationResult;
import com.deok9labs.aster.domain.model.TransformationSource;

/**
 * 정수와 문자열에 변환 규칙을 적용하는 inbound port다.
 */
public interface TransformationUseCase {

    /**
     * 변환 규칙을 적용한 결과를 반환한다.
     *
     * <p>외부 상태를 읽거나 변경하지 않으므로 같은 입력에는 항상 같은 결과를 반환한다.
     * 유효한 {@link TransformationSource}에 대해서는 실패하지 않는다.</p>
     *
     * @param source 변환할 정수와 문자열
     * @return 정수는 2배, 문자열은 뒤집은 결과
     */
    TransformationResult transform(TransformationSource source);
}
