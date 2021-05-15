package org.myongoingscalendar.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.myongoingscalendar.model.AjaxResponse;
import org.myongoingscalendar.model.ResponseStatus;
import org.myongoingscalendar.security.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerConfig {

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public void processHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        log.error(ex.getMessage(), ex);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void processClientAbortException(ClientAbortException ex) {
        log.error(ex.getMessage(), ex);
        log.error("Client disconnected");
    }

    @ExceptionHandler(NoNodeAvailableException.class)
    public AjaxResponse<?> processNoNodeAvailableException() {
        return new AjaxResponse<>(ResponseStatus.S10015.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResponse<?> processException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return new AjaxResponse<>(ResponseStatus.S10016.getStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public AjaxResponse<?> processAuthenticationException(AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return new AjaxResponse<>(ResponseStatus.S10012.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResponse<?> processAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new AjaxResponse<>(ResponseStatus.S10012.getStatus());
    }

}