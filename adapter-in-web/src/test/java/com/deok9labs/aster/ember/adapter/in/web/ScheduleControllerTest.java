package com.deok9labs.aster.ember.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deok9labs.aster.ember.application.port.in.GetCurrentScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.result.CurrentScheduleResult;
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
        GetCurrentScheduleUseCase getUseCase = scheduleWeek -> new CurrentScheduleResult(
                scheduleWeek == com.deok9labs.aster.ember.domain.ScheduleWeek.CURRENT
                        ? LocalDate.of(2026, 9, 14) : LocalDate.of(2026, 9, 21),
                scheduleWeek == com.deok9labs.aster.ember.domain.ScheduleWeek.CURRENT
                        ? LocalDate.of(2026, 9, 20) : LocalDate.of(2026, 9, 27),
                List.of(),
                List.of(new CurrentScheduleResult.Availability(
                        1,
                        LocalDate.of(2026, 9, 21),
                        List.of(new CurrentScheduleResult.TimeRange(
                                ScheduleTime.parse("21:30"), ScheduleTime.parse("24:00"))))));
        ReplaceMemberScheduleUseCase replaceUseCase = command -> new ReplaceMemberScheduleResult(
                command.memberId(), command.expectedWeekStart(), 1,
                LocalDateTime.of(2026, 9, 18, 21, 30));
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new ScheduleController(getUseCase, replaceUseCase))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void getsNextWeekScheduleWithMidnightRangeEnd() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weekStart").value("2026-09-21"))
                .andExpect(jsonPath("$.availability[0].ranges[0].startTime").value("21:30"))
                .andExpect(jsonPath("$.availability[0].ranges[0].endTime").value("24:00"));
    }

    @Test
    void replacesMemberSchedule() throws Exception {
        mockMvc.perform(put("/api/v1/schedules/next/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "expectedWeekStart": "2026-09-21",
                                  "ranges": [
                                    {"date": "2026-09-21", "startTime": "21:30", "endTime": "24:00"}
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
                                  "ranges": [
                                    {"date": "2026-09-18", "startTime": "invalid", "endTime": "19:00"}
                                  ]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_SCHEDULE_REQUEST"));
    }
}
