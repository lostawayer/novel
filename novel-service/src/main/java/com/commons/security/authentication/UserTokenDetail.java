package com.commons.security.authentication;

import lombok.Data;

import java.io.Serializable;
import java.util.Locale;

@Data
public class UserTokenDetail implements Serializable {
    private String ipAddress;
    private Locale locale;
    private String accssToken;
}
