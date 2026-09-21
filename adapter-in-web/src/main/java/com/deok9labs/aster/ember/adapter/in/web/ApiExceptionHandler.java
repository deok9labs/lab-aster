package com.deok9labs.aster.ember.adapter.in.web;

import com.deok9labs.aster.ember.application.service.ScheduleWeekMismatchException;
import com.deok9labs.aster.ember.application.service.MemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** API 내부 예외를 일관되고 안전한 HTTP 오류 계약으로 변환한다. */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MemberNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleMemberNotFound(MemberNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler(ScheduleWeekMismatchException.class)
    ResponseEntity<ApiErrorResponse> handleScheduleWeekMismatch(ScheduleWeekMismatchException exception) {
        return error(HttpStatus.CONFLICT, "SCHEDULE_WEEK_MISMATCH", exception.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
    })
    ResponseEntity<ApiErrorResponse> handleInvalidRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE_REQUEST", "일정 요청 값이 올바르지 않습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidBusinessValue(IllegalArgumentException exception) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE_REQUEST", exception.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(code, message));
    }

    public record ApiErrorResponse(String code, String message) {
    }
}
