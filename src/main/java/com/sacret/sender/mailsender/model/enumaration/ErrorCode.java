package com.sacret.sender.mailsender.model.enumaration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)

public enum ErrorCode {

    ERR_FILE("Can not read file"),
    ERR_EMAIL_TEMPLATE("Can not read template"),
    ERR_PARAM_INVALID("Validation failed"),
    ERR_EMAIL_RECEIVED("Received invalid email values"),
    ERR_EMAIL_DB("No emails in db");

    @Getter
    final String value;

    ErrorCode(String value) {
        this.value = value;
    }
}
