package com.deok9labs.aster.adapter.out.logging;

import com.deok9labs.aster.application.port.out.TempGreetingLogPort;
import com.deok9labs.aster.domain.model.TempGreeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 인사말 기록 요청을 application 로그로 전달하는 임시 outbound adapter다.
 *
 * <p>실제 기록 대상이 정해지면 해당 외부 시스템 adapter로 교체하거나 제거한다.</p>
 */
public class TempGreetingLogAdapter implements TempGreetingLogPort {

    private static final Logger log = LoggerFactory.getLogger(TempGreetingLogAdapter.class);

    /**
     * application 로그를 사용하는 adapter를 생성한다.
     */
    public TempGreetingLogAdapter() {
    }

    @Override
    public void log(TempGreeting greeting) {
        log.info("{}", greeting.message());
    }
}
