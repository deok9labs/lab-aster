/**
 * Ember application port를 JPA와 PostgreSQL로 구현하는 outbound adapter다.
 *
 * <p>JPA entity와 Spring Data repository는 이 패키지 밖으로 노출하지 않는다. 일정 교체 port는 기존 slot
 * 삭제, submission 갱신과 새 slot 저장을 transaction 하나로 보장한다.</p>
 */
package com.deok9labs.aster.ember.adapter.out.persistence;
