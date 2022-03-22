package com.sacret.sender.mailsender.model.dto;

import com.sacret.sender.mailsender.model.dto.customvalidator.CountOrEmails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Positive;
import java.util.Set;

@Getter
@Setter
@ToString
@CountOrEmails
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobRequestDTO {
    @Positive
    Integer count;
    String subject;
    String text;
    Set<String> emails;
}
