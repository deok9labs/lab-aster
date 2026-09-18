package com.deok9labs.aster.ember.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HttpRequestLoggingFilterTest {

    private final HttpRequestLoggingFilter filter = new HttpRequestLoggingFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void addsRequestIdAndMakesItAvailableWhileRequestIsProcessed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/schedules/current");
        request.setQueryString("private=value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> requestIdSeenByApplication = new AtomicReference<>();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            requestIdSeenByApplication.set(MDC.get(HttpRequestLoggingFilter.REQUEST_ID_MDC_KEY));
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_ACCEPTED);
        });

        assertThat(response.getHeader(HttpRequestLoggingFilter.REQUEST_ID_HEADER))
                .isEqualTo(requestIdSeenByApplication.get());
        assertThat(requestIdSeenByApplication.get()).isNotBlank();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_ACCEPTED);
        assertThat(MDC.get(HttpRequestLoggingFilter.REQUEST_ID_MDC_KEY)).isNull();
    }
}
