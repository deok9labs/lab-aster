package com.deok9labs.aster.ember.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deok9labs.aster.ember.application.port.in.GetScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.result.ScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ScheduleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        GetScheduleUseCase getUseCase = scheduleWeek -> new ScheduleResult(
                scheduleWeek == com.deok9labs.aster.ember.domain.ScheduleWeek.CURRENT
                        ? LocalDate.of(2026, 9, 14) : LocalDate.of(2026, 9, 21),
                scheduleWeek == com.deok9labs.aster.ember.domain.ScheduleWeek.CURRENT
                        ? LocalDate.of(2026, 9, 20) : LocalDate.of(2026, 9, 27),
                List.of(),
                List.of(new ScheduleResult.Availability(
                        1,
                        LocalDate.of(2026, 9, 21),
                        List.of(ScheduleTime.parse("21:30"), ScheduleTime.parse("24:00")))));
        ReplaceMemberScheduleUseCase replaceUseCase = command -> new ReplaceMemberScheduleResult(
                command.memberId(), command.expectedWeekStart(), 1,
                LocalDateTime.of(2026, 9, 18, 21, 30));
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ScheduleController(getUseCase, replaceUseCase))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getsNextWeekScheduleWithMidnightSlot() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-09-21"))
                .andExpect(jsonPath("$.availability[0].slots[0]").value("21:30"))
                .andExpect(jsonPath("$.availability[0].slots[1]").value("24:00"));
    }

    @Test
    void replacesMemberSchedule() throws Exception {
        mockMvc.perform(put("/api/v1/schedules/next/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "expectedWeekStart": "2026-09-21",
                                  "slots": [
                                    {"date": "2026-09-21", "slotTime": "21:30"},
                                    {"date": "2026-09-21", "slotTime": "24:00"}
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.updatedAt").value("2026-09-18T21:30:00+09:00"));
    }

    @Test
    void rejectsMalformedSlotTime() throws Exception {
        mockMvc.perform(put("/api/v1/schedules/current/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "expectedWeekStart": "2026-09-14",
                                  "slots": [
                                    {"date": "2026-09-18", "slotTime": "invalid"}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_SCHEDULE_REQUEST"));
    }
}
