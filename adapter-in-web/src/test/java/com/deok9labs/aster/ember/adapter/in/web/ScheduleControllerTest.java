package com.deok9labs.aster.ember.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deok9labs.aster.ember.application.port.in.GetCurrentScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
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
        GetCurrentScheduleUseCase getUseCase = () -> new CurrentScheduleResult(
                LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 20), List.of(), List.of());
        ReplaceMemberScheduleUseCase replaceUseCase = command -> new ReplaceMemberScheduleResult(
                command.memberId(), command.weekStart(), 1,
                LocalDateTime.of(2026, 9, 18, 21, 30));
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ScheduleController(getUseCase, replaceUseCase))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void replacesMemberSchedule() throws Exception {
        mockMvc.perform(put("/api/v1/schedules/current/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weekStart": "2026-09-14",
                                  "slots": [{"date": "2026-09-18", "time": "19:00"}]
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
                                  "weekStart": "2026-09-14",
                                  "slots": [{"date": "2026-09-18", "time": "invalid"}]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_SCHEDULE_REQUEST"));
    }
}
