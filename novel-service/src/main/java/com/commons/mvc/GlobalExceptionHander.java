package com.commons.mvc;

import com.commons.mvc.WebResult;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@CommonsLog
public class GlobalExceptionHander {
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<WebResult> handleException(WebRequest requset, Throwable ex){
        String error=ExceptionUtils.getMessage(ex);
        log.error(error);
        WebResult webResult =WebResult.createFailureWebresult(error);
        return ResponseEntity.ok().body(webResult);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<WebResult> handleConstraintViolationException(WebRequest requset,ConstraintViolationException ex){
        List<String>messagesList=new ArrayList<>();
        ex.getConstraintViolations().forEach(constraintViolation-> {
            messagesList.add(constraintViolation.getMessage());
        });
        String error= StringUtils.join(messagesList,",");
        return ResponseEntity.ok(WebResult.createFailureWebresult(error));
    }
}
