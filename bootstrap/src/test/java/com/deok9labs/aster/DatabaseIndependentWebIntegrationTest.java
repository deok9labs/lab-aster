package com.deok9labs.aster;

import com.deok9labs.aster.ember.adapter.out.persistence.repository.AvailabilitySubmissionsJpaRepository;
import com.deok9labs.aster.ember.adapter.out.persistence.repository.MembersJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/** Database 연결이 검증 목적이 아닌 Spring web 통합 테스트의 공통 격리 설정이다. */
abstract class DatabaseIndependentWebIntegrationTest {

    @MockitoBean
    MembersJpaRepository membersRepository;

    @MockitoBean
    AvailabilitySubmissionsJpaRepository submissionsRepository;

    @MockitoBean
    JdbcTemplate jdbcTemplate;
}
