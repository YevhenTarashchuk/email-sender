package com.sacret.sender.mailsender.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {
    public enum Status{ ERROR, OK}

    private Status status;
    private T data;
    private ErrorCode error;
    private String errorMsg;
    private List<FieldErrorVM> fieldErrors;

}
