package com.deok9labs.aster.ember.adapter.in.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * HTTP 요청의 처리 결과와 소요 시간을 애플리케이션 로그에 기록하는 웹 경계 필터다.
 *
 * <p>요청·응답 본문, 쿼리 문자열과 인증 헤더는 민감정보 노출을 막기 위해 기록하지 않는다. 서버가 생성한 요청 ID는
 * 응답 헤더와 MDC에 함께 제공되어 같은 요청에서 발생한 다른 로그를 연관 지을 수 있다.</p>
 */
@Component
final class HttpRequestLoggingFilter extends OncePerRequestFilter {

    static final String REQUEST_ID_HEADER = "X-Request-Id";
    static final String REQUEST_ID_MDC_KEY = "requestId";

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long startedAt = System.nanoTime();

        response.setHeader(REQUEST_ID_HEADER, requestId);
        MDC.put(REQUEST_ID_MDC_KEY, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            try {
                long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);

                // URI만 기록하여 쿼리 파라미터에 포함될 수 있는 사용자 입력과 민감정보를 로그에서 제외한다.
                log.info(
                        "HTTP request completed: requestId={}, method={}, path={}, status={}, durationMs={}",
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        durationMs
                );
            } finally {
                // 서블릿 스레드는 재사용되므로 다음 요청에 현재 요청 ID가 전파되지 않도록 반드시 제거한다.
                MDC.remove(REQUEST_ID_MDC_KEY);
            }
        }
    }
}
