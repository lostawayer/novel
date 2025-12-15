package com.commons.security.rest;

import com.commons.mvc.WebResult;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import com.commons.security.SecurityConfig;

@RestController
public class AuthController {


    @PostMapping(SecurityConfig.login_SUCCESS)
    public WebResult<Authentication> loginSuccess(@RequestAttribute Authentication authentication){
        return WebResult.createSuccessWebResult(authentication);
    }
    @PostMapping(SecurityConfig.login_Faiure)
    public WebResult<String> loginFaiure(@RequestAttribute String error){
        return WebResult.createFailureWebresult(error);
    }
    @RequestMapping(value=SecurityConfig.UN_Login)
    public WebResult<String> requireAuthentication(@RequestAttribute(required = false) String error){
        return WebResult.createFailureWebresult(error);
    }
    @GetMapping(SecurityConfig.logout_SUCCESS)
    public WebResult<Authentication> logoutSuccess(@RequestAttribute(required = false) Authentication authentication){
        return WebResult.createSuccessWebResult(authentication);
    }
}
