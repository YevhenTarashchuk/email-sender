package com.sacret.sender.mailsender.exception;

import com.sacret.sender.mailsender.model.enumaration.ErrorCode;
import com.sacret.sender.mailsender.model.response.CustomResponse;
import com.sacret.sender.mailsender.model.response.FieldErrorVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
                .map(f -> FieldErrorVM.builder().objectName(f.getObjectName()).field(f.getField()).message(f.getCode()).build())
                .collect(toList());

        CustomResponse errorResponse = CustomResponse.builder()
                .status(CustomResponse.Status.ERROR)
                .error(ErrorCode.ERR_PARAM_INVALID)
                .errorMsg(ErrorCode.ERR_PARAM_INVALID.getValue())
                .fieldErrors(fieldErrors).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        List<FieldErrorVM> fieldErrors = constraintViolations.stream()
                .map(constraintViolation -> FieldErrorVM.builder()
                        .value(constraintViolation.getInvalidValue())
                        .message(constraintViolation.getMessage())
                        .property(constraintViolation.getPropertyPath().toString())
                        .build())
                .collect(toList());

        CustomResponse errorResponse = CustomResponse.builder()
                .status(CustomResponse.Status.ERROR)
                .error(ErrorCode.ERR_PARAM_INVALID).fieldErrors(fieldErrors).build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<CustomResponse> handleShellBaseException(BaseException ex) {
        CustomResponse errorResponse = CustomResponse.builder()
                .status(CustomResponse.Status.ERROR)
                .error(ex.getErrorCode())
                .errorMsg(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
}
