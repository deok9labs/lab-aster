/**
 * Ember 유스케이스를 HTTP로 노출하는 inbound adapter다.
 *
 * <p>이 패키지는 HTTP 형식 검증과 Web DTO 변환만 담당하고 application inbound port를 호출한다. JPA entity,
 * repository와 persistence adapter를 직접 참조하거나 transaction과 business 규칙을 처리하지 않는다. HTTP 요청 로깅은 본문과
 * 쿼리 문자열을 제외하고 요청 식별자, 경로, 응답 상태와 처리 시간만 기록한다.</p>
 */
package com.deok9labs.aster.ember.adapter.in.web;
