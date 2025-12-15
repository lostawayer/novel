package com.commons.mvc;

import lombok.Data;

import java.io.Serializable;

@Data
public class WebResult<T> implements Serializable {
    private boolean success;
    private String error;
    private T data;
    private WebResult(){}
    public static<T> WebResult<T> createSuccessWebResult(T data){
        WebResult<T> webResult = new WebResult<>();
        webResult.setSuccess(true);
        webResult.setData(data);
        return webResult;
    }
    public static<T> WebResult<T> createSuccessWebResult(){
        WebResult<T> webResult = new WebResult<>();
        webResult.setSuccess(true);
        //webResult.setData(null);
        return webResult;
    }
    public static<T> WebResult<T> createFailureWebresult(String error){
        WebResult<T> webResult=new WebResult<>();
        webResult.setSuccess(false);
        webResult.setError(error);
        return webResult;
    }
}
