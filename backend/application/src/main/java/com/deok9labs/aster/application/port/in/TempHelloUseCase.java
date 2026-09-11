package com.deok9labs.aster.application.port.in;

import com.deok9labs.aster.domain.model.TempGreeting;

/**
 * 임시 인사말 생성을 요청하는 inbound port다.
 *
 * <p>헥사고날 아키텍처의 기본 호출 흐름을 검증하기 위한 경계이며 실제 유스케이스가 도입되면 제거한다.</p>
 */
public interface TempHelloUseCase {

    /**
     * 인사말을 생성하고 필요한 외부 기록을 완료한다.
     *
     * @return 외부 기록이 완료된 인사말
     */
    TempGreeting sayHello();
}
