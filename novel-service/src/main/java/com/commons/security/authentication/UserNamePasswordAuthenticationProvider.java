package com.commons.security.authentication;

import com.commons.security.util.JwtUtil;
import com.domain.User;
import com.service.IUserManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import com.commons.security.SecurityConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.commons.security.SecurityConfig.*;

import java.util.List;
import java.util.Locale;

@Service
public class UserNamePasswordAuthenticationProvider implements AuthenticationProvider , MessageSourceAware {
    @Autowired
    @Qualifier(IUserManager.ID)
    private IUserManager userManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MessageSource messageSource;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {//authenticate（验证逻辑）
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;//将通用的Authentication对象转换为具体的UsernamePasswordAuthenticationToken类型，以便后续提取用户名和密码进行验证。
        UserTokenDetail detail=(UserTokenDetail) token.getDetails();
        Locale locale=detail.getLocale();
        String username=token.getPrincipal().toString();
        String credentials =token.getCredentials().toString();//credentials是用户输入的原始密码
        if (StringUtils.isBlank(username)){
            String message=this.messageSource.getMessage("security.login.username.empty",null,locale);
            BadCredentialsException exception=new BadCredentialsException(message);
            throw exception;
        }
        if (StringUtils.isBlank(credentials)){
            String message=this.messageSource.getMessage("security.login.password.empty",null,locale);
            BadCredentialsException exception=new BadCredentialsException(message);
            throw exception;
        }
        //Spring Security 身份验证流程中的核心验证逻辑，用于完成 “查询用户信息” 和 “验证密码” 的关键步骤，是对之前空值校验的补充。
        User user= this.userManager.getUserByName(username);
        if (user==null){
            String message=this.messageSource.getMessage("security.login.user.not-found",new String[]{username},locale);
            AuthenticationServiceException exception=new AuthenticationServiceException(message);
            throw exception;
        }
        String password=user.getPassword();//user.getPassword()获取数据库中存储的加密后的密码
        if (!this.passwordEncoder.matches(credentials,password)){
            String message=this.messageSource.getMessage("security.login.password.wrong",null,locale);
            BadCredentialsException exception=new BadCredentialsException(message);
            throw exception;
        }
        //设置用户权限
        List<String >roleName=this.userManager.findRoleName(username);
        if (roleName.isEmpty()){
            String message=this.messageSource.getMessage("security.login.user.no-authority",null,locale);
            BadCredentialsException exception=new BadCredentialsException(message);
            throw exception;
        }

        List<GrantedAuthority> authorities=AuthorityUtils.createAuthorityList(roleName);
        UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(username,password,authorities);
        String accessToken= JwtUtil.encodeJwt("medical",username,password,3600);
        detail.setAccssToken(accessToken);
        authToken.setDetails(detail);
        return authToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {//supports（支持的验证类型）方法
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource=messageSource;
    }
}