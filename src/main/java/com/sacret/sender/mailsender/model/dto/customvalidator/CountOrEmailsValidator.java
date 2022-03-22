package com.sacret.sender.mailsender.model.dto.customvalidator;

import com.sacret.sender.mailsender.model.dto.JobRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountOrEmailsValidator implements ConstraintValidator<CountOrEmails, JobRequestDTO> {

    @Override
    public void initialize(CountOrEmails constraintAnnotation) {
        //not required
    }

    @Override
    public boolean isValid(JobRequestDTO dto, ConstraintValidatorContext context) {
        return ( dto.getEmails() != null && dto.getCount() == null ) || ( dto.getEmails() == null && dto.getCount() != null );
    }
}
