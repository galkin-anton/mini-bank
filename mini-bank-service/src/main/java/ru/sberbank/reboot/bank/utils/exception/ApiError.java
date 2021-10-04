package ru.sberbank.reboot.bank.utils.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Класс ошибок
 */
@Getter
@Slf4j
class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss.SSSXXX")
    private final ZonedDateTime timestamp;
    private HttpStatus status;
    private String debugMessage;

    private ApiError() {
        timestamp = ZonedDateTime.now();
    }

    ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    ApiError(HttpStatus status, Throwable ex) {
        this(status);
        this.debugMessage = ex.getLocalizedMessage();
        log.error(this.debugMessage, ex);
    }
}