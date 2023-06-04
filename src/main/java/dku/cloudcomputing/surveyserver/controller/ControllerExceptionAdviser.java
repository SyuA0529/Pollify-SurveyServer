package dku.cloudcomputing.surveyserver.controller;

import dku.cloudcomputing.surveyserver.controller.dto.BindingErrorResponseDto;
import dku.cloudcomputing.surveyserver.controller.dto.FieldBindingErrorDto;
import dku.cloudcomputing.surveyserver.controller.dto.StatusResponseDto;
import dku.cloudcomputing.surveyserver.exception.ClientOccurException;
import dku.cloudcomputing.surveyserver.exception.dto.FieldBindException;
import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionAdviser {
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoSuchMemberException.class)
    public StatusResponseDto noSuchMemberExceptionHandler(NoSuchMemberException exception) {
        return new StatusResponseDto("fail");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FieldBindException.class)
    public BindingErrorResponseDto fieldBindExceptionHandler(FieldBindException bindException) {
        BindingErrorResponseDto bindingErrorResponseDto = new BindingErrorResponseDto("fail");
        bindingErrorResponseDto.getErrors().addAll(bindException.getFieldErrors().stream()
                .map(e -> new FieldBindingErrorDto(e.getField(), e.getDefaultMessage()))
                .toList());
        return bindingErrorResponseDto;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ClientOccurException.class)
    public StatusResponseDto clientOccurHandler(ClientOccurException clientOccurException) {
        return new StatusResponseDto("fail");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public StatusResponseDto runtimeExceptionHandler(RuntimeException runtimeException) {
        runtimeException.printStackTrace();
        return new StatusResponseDto("fail");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    public StatusResponseDto fieldDBIntegrityExceptionHandler(
            JdbcSQLIntegrityConstraintViolationException integrityViolationException) {
        return new StatusResponseDto("fail");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public StatusResponseDto dataIntegrityException(DataIntegrityViolationException exception) {
        return new StatusResponseDto("fail");
    }
}