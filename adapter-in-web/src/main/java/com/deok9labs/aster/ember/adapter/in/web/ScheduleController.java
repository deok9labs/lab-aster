package com.deok9labs.aster.ember.adapter.in.web;

import com.deok9labs.aster.ember.domain.ScheduleSlot;
import com.deok9labs.aster.ember.domain.ScheduleTime;
import com.deok9labs.aster.ember.domain.ScheduleWeek;
import com.deok9labs.aster.ember.application.port.in.GetScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.ReplaceMemberScheduleUseCase;
import com.deok9labs.aster.ember.application.port.in.command.ReplaceMemberScheduleCommand;
import com.deok9labs.aster.ember.application.port.in.result.ScheduleResult;
import com.deok9labs.aster.ember.application.port.in.result.ReplaceMemberScheduleResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 이번 주와 다음 주 일정 조회 및 팀원 일정 교체를 제공하는 REST adapter다. */
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");
    private final GetScheduleUseCase getScheduleUseCase;
    private final ReplaceMemberScheduleUseCase replaceMemberScheduleUseCase;

    public ScheduleController(
            GetScheduleUseCase getScheduleUseCase,
            ReplaceMemberScheduleUseCase replaceMemberScheduleUseCase) {
        this.getScheduleUseCase = getScheduleUseCase;
        this.replaceMemberScheduleUseCase = replaceMemberScheduleUseCase;
    }

    @GetMapping("/{scheduleWeek}")
    public ScheduleResponse getSchedule(
            @Parameter(schema = @Schema(allowableValues = {"current", "next"}))
            @PathVariable("scheduleWeek") String scheduleWeek) {
        ScheduleResult result = getScheduleUseCase.getSchedule(
                ScheduleWeek.fromPath(scheduleWeek));

        // Application 결과가 HTTP 표현 형식에 결합되지 않도록 Web response로 변환한다.
        return new ScheduleResponse(
                result.weekStart(),
                result.weekEnd(),
                result.members().stream().map(member -> new MemberResponse(
                        member.id(), member.name(), member.server(), member.position(),
                        member.submitted(), toOffsetDateTime(member.updatedAt()))).toList(),
                result.availability().stream().map(availability -> new AvailabilityResponse(
                        availability.memberId(),
                        availability.date(),
                        availability.slots().stream().map(ScheduleTime::toString).toList()))
                        .toList());
    }

    @PutMapping("/{scheduleWeek}/members/{memberId}")
    public ReplaceScheduleResponse replaceMemberSchedule(
            @Parameter(schema = @Schema(allowableValues = {"current", "next"}))
            @PathVariable("scheduleWeek") String scheduleWeek,
            @PathVariable("memberId") @Positive int memberId,
            @Valid @RequestBody ReplaceScheduleRequest request) {
        // 형식 검증이 끝난 Web DTO를 domain 규칙이 적용되는 application command로 바꾼다.
        ReplaceMemberScheduleResult result = replaceMemberScheduleUseCase.replaceMemberSchedule(
                new ReplaceMemberScheduleCommand(
                        memberId,
                        ScheduleWeek.fromPath(scheduleWeek),
                        request.expectedWeekStart(),
                        request.slots().stream()
                                .map(slot -> new ScheduleSlot(
                                        slot.date(),
                                        ScheduleTime.parse(slot.slotTime())))
                                .toList()));
        return new ReplaceScheduleResponse(
                result.memberId(),
                result.weekStart(),
                result.revision(),
                toOffsetDateTime(result.updatedAt()));
    }

    private OffsetDateTime toOffsetDateTime(java.time.LocalDateTime value) {
        // DB timestamp에는 zone이 없으므로 외부 응답에서는 서비스 기준 offset을 명시한다.
        return value == null ? null : value.atZone(SERVICE_ZONE).toOffsetDateTime();
    }

    public record ReplaceScheduleRequest(
            @NotNull LocalDate expectedWeekStart,
            @NotNull List<@Valid SlotRequest> slots) {
    }

    public record SlotRequest(
            @NotNull LocalDate date,
            @NotBlank
            @Schema(
                    type = "string",
                    pattern = "^(?:18|19|20|21|22|23):(?:00|30)$|^24:00$",
                    example = "24:00")
            String slotTime) {
    }

    public record ScheduleResponse(
            LocalDate weekStart,
            LocalDate weekEnd,
            List<MemberResponse> members,
            List<AvailabilityResponse> availability) {
    }

    public record MemberResponse(
            int id,
            String name,
            String server,
            String position,
            boolean submitted,
            OffsetDateTime updatedAt) {
    }

    public record AvailabilityResponse(
            int memberId,
            LocalDate date,
            @ArraySchema(schema = @Schema(
                    type = "string",
                    pattern = "^(?:18|19|20|21|22|23):(?:00|30)$|^24:00$",
                    example = "24:00"))
            List<String> slots) {
    }

    public record ReplaceScheduleResponse(
            int memberId,
            LocalDate weekStart,
            int revision,
            OffsetDateTime updatedAt) {
    }
}
