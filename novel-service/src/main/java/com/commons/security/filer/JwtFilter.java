package com.commons.security.filer;

import com.commons.security.authentication.UserTokenDetail;
import com.commons.security.util.JwtUtil;
import com.domain.User;
import com.service.IUserManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.commons.security.SecurityConfig.UN_Login;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    @Qualifier(IUserManager.ID)
    private IUserManager userManager;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${medical.security.expiressSecond}")
    private int expiressSecond;

    @Autowired
    private MessageSource messageSource;

    private void tokenIsNoValid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);//设置响应的内容类型为 JSON（application/json)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//设置 HTTP 响应状态码为 401（未授权），明确标识这是一个认证失败的请求。
        String error=this.messageSource.getMessage("security.jwt.invalid",null,request.getLocale());
        request.setAttribute("error",error);
        request.getRequestDispatcher(UN_Login).forward(request,response);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //request.getHeader("Authorization");
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isNotBlank(authorization) && StringUtils.startsWithIgnoreCase(authorization, "Bearer ")) {
            String token = StringUtils.substringAfter(authorization, "Bearer ");
            if (StringUtils.isBlank(token)) {
                tokenIsNoValid(request,response);
            }
            String username = JwtUtil.decodeJwtSubject(token);
            if (StringUtils.isBlank(username)) {
                tokenIsNoValid(request,response);
            }
            User user = this.userManager.getUserByName(username);
            if (user == null) {
                tokenIsNoValid(request,response);
            }
            String password = user.getPassword();
            boolean valid=JwtUtil.decodeJwt(password,token);
            if (!valid) {
                tokenIsNoValid(request,response);
            }
            List<String >roleName=this.userManager.findRoleName(username);
            List<GrantedAuthority> authorities= AuthorityUtils.createAuthorityList(roleName);
            UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(username,password,authorities);
            UserTokenDetail detail=new UserTokenDetail();
            detail.setLocale(request.getLocale());
            detail.setIpAddress(request.getRemoteAddr());
            String accessToken= JwtUtil.encodeJwt(applicationName,username,password,expiressSecond);
            detail.setAccssToken(accessToken);
            authToken.setDetails(detail);
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request,response);
            } else {
                filterChain.doFilter(request,response);
            }
        }
    }
