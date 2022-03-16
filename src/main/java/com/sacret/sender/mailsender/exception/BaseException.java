package com.sacret.sender.mailsender.exception;

import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString(callSuper = true)
public class BaseException extends RuntimeException {

    HttpStatus httpStatus;
    ErrorCode errorCode;

    public BaseException(HttpStatus httpStatus, ErrorCode errorCode) {

        super(errorCode.getValue());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;

        LOG.warn(errorCode.getValue());
    }
}
