package com.commons.security;

import com.commons.security.authentication.UserNamePasswordAuthenticationProvider;
import com.commons.security.authentication.UserTokenDetail;
import com.commons.security.filer.JwtFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.util.pattern.PathPattern;

import java.io.IOException;

@Configuration
@ComponentScan({
        "com.commons.security.authentication",
        "com.commons.security.rest",
        "com.commons.listener",
        "com.commons.security.filer"
})
@ConfigurationProperties(prefix = "medical.security")
public class SecurityConfig {
//    @Value("${medical.security.no-auth-urls}")
    @Getter @Setter
    public String[] noAuthurls;
    @Value("${medical.security.jwt-supoprt}")
    private boolean jwtSuupprt;

    public static final String login_SUCCESS="/loginSuccess";
    public static final String login_Faiure="/loginFaiure";
    public static final String logout_SUCCESS="/logoutSuccess";
    public static final String UN_Login="/requireAuthentication";
    @Bean
    public AuthenticationManager authenticationManager(
            UserNamePasswordAuthenticationProvider userNamePasswordAuthenticationProvider
    ){
        return new ProviderManager(userNamePasswordAuthenticationProvider);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtFilter jwtFilter) throws Exception {
        if (this.jwtSuupprt){
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            http.sessionManagement(seeion->{
                seeion.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            });
        }else {
            http.sessionManagement(Customizer.withDefaults());
        }

        http.cors(Customizer.withDefaults());//使用withDefaults的跨域配置
        http.csrf(csrf ->csrf.disable()); //csrf跨域请求伪造(关闭)
        http.httpBasic(httpSecurityHttpBasicConfigurer -> {
            httpSecurityHttpBasicConfigurer.disable();//关闭跨域请求访问,禁用Basic认证
        });
        http.formLogin(formLogin -> {
            formLogin.loginProcessingUrl("/userlogin");
            formLogin.usernameParameter("username");
            formLogin.passwordParameter("password");
            formLogin.successHandler((request, response, authentication) -> {
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);
               request.setAttribute("authentication",authentication);
               request.getRequestDispatcher(login_SUCCESS).forward(request,response);
            });
            formLogin.failureHandler((request, response, exception) -> {
               response.setContentType(MediaType.APPLICATION_JSON_VALUE);//设置响应的内容类型为 JSON（application/json)
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//设置 HTTP 响应状态码为 401（未授权），明确标识这是一个认证失败的请求。
               request.setAttribute("error",exception.getMessage());
               request.getRequestDispatcher(login_Faiure).forward(request,response);//将请求转发到/loginFailure路径对应的处理器
            });
            formLogin.authenticationDetailsSource(context -> {
                UserTokenDetail detail=new UserTokenDetail();
                detail.setLocale(context.getLocale());
                detail.setIpAddress(context.getRemoteAddr());
                return detail;
            });
        });
            http.exceptionHandling(exception -> {
                exception.authenticationEntryPoint((request, response, authEception) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);//设置响应的内容类型为 JSON（application/json)
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//设置 HTTP 响应状态码为 401（未授权），明确标识这是一个认证失败的请求。
                    request.setAttribute("error",authEception.getMessage());
                    request.getRequestDispatcher(UN_Login).forward(request,response);
                });
            });
            http.logout(logout -> {
                logout.logoutUrl("/userlogout");
               logout.logoutSuccessHandler(new LogoutSuccessHandler() {
                   @Override
                   public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                       response.setContentType(MediaType.APPLICATION_JSON_VALUE);//设置响应的内容类型为 JSON（application/json)
                       request.setAttribute("authentication", authentication);
                       request.getRequestDispatcher(logout_SUCCESS).forward(request, response);
                   }
               });
            });
        http.authorizeHttpRequests(auth ->{
            //org.springframework.security.web.servlet.util.matcher
            PathPatternRequestMatcher.Builder mvc= PathPatternRequestMatcher.withDefaults();
            auth.requestMatchers(this.noAuthurls).permitAll();//不需要认证,requestMatchers()必须在anyRequest()之前调用
            auth.requestMatchers(//requestMatchers() 方法用于指定一组需要应用特定安全规则的请求路径或 URL 模式
                    mvc.matcher(login_Faiure),
                    mvc.matcher(UN_Login),
                    mvc.matcher(logout_SUCCESS),
                    PathRequest.toStaticResources().atCommonLocations()//允许静态资源访问
            ).permitAll();
            auth.anyRequest().authenticated();//任何请求都要身份认证
        });
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}