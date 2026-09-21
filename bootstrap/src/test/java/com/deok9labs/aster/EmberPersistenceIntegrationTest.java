package com.deok9labs.aster;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.deok9labs.aster.ember.adapter.out.persistence.EmberPersistenceAdapter;
import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.WeekPeriod;
import com.deok9labs.aster.ember.application.port.out.LoadSchedulePort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/** 실제 PostgreSQL schema와 JPA 매핑 및 일정 전체 교체 transaction을 검증한다. */
@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class EmberPersistenceIntegrationTest {

    private static final int MEMBER_ID = 2_147_483_000;
    private static final LocalDate WEEK_START = LocalDate.of(2026, 9, 14);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmberPersistenceAdapter persistenceAdapter;

    @BeforeEach
    void setUpReferenceData() {
        // 고정 식별자는 rollback되는 test transaction 안에서만 사용해 운영 데이터와 충돌하지 않는다.
        jdbcTemplate.update("delete from members where id = ?", MEMBER_ID);
        jdbcTemplate.update("""
                insert into common_codes
                    (code_group, code, code_name, sort_order, enabled)
                values ('POSITION', 'TEST', 'Test', 9999, true)
                on conflict (code_group, code) do nothing
                """);
        jdbcTemplate.update("""
                insert into common_codes
                    (code_group, code, code_name, sort_order, enabled)
                values ('ACTIVE_STATUS', 'ACTIVE', 'Active', 0, true)
                on conflict (code_group, code) do nothing
                """);
        jdbcTemplate.update("""
                insert into members
                    (id, display_name, server_name, position_code, active_code)
                overriding system value
                values (?, 'Integration Test', 'Test', 'TEST', 'ACTIVE')
                """, MEMBER_ID);
    }

    @Test
    void replacesSlotsAndAdvancesRevision() {
        WeekPeriod week = new WeekPeriod(WEEK_START, WEEK_START.plusDays(6));
        LocalDateTime firstUpdate = LocalDateTime.of(2026, 9, 18, 20, 0);
        persistenceAdapter.replaceSchedule(
                MEMBER_ID,
                week,
                List.of(
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("18:00")),
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("24:00"))),
                firstUpdate).orElseThrow();

        LocalDateTime secondUpdate = firstUpdate.plusMinutes(1);
        persistenceAdapter.replaceSchedule(
                MEMBER_ID,
                week,
                List.of(
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("18:00")),
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("18:30")),
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("19:00")),
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("23:30")),
                        new ScheduleSlot(LocalDate.of(2026, 9, 18), ScheduleTime.parse("24:00"))),
                secondUpdate).orElseThrow();

        LoadSchedulePort.ScheduleData loaded = persistenceAdapter.loadSchedule(week);
        LoadSchedulePort.MemberData member = loaded.members().stream()
                .filter(candidate -> candidate.id() == MEMBER_ID)
                .findFirst()
                .orElseThrow();
        LoadSchedulePort.AvailabilityData availability = loaded.availability().stream()
                .filter(candidate -> candidate.memberId() == MEMBER_ID)
                .findFirst()
                .orElseThrow();

        assertEquals(5, availability.slots().size());
        assertEquals(ScheduleTime.parse("18:00"), availability.slots().getFirst());
        assertEquals(ScheduleTime.parse("24:00"), availability.slots().getLast());
        assertEquals(1, jdbcTemplate.queryForObject(
                """
                        select count(*) from availability_slots
                        where member_id = ? and week_start = ?
                          and available_date = ? and slot_time = time '24:00'
                        """,
                Integer.class,
                MEMBER_ID,
                WEEK_START,
                LocalDate.of(2026, 9, 18)));
        assertEquals(secondUpdate, member.updatedAt());
        assertEquals(2, jdbcTemplate.queryForObject(
                """
                        select revision from availability_submissions
                        where member_id = ? and week_start = ?
                        """, Integer.class, MEMBER_ID, WEEK_START));
    }
}
