package com.commons.listener;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener  {
    @EventListener({AuthenticationSuccessEvent.class})
    @Async
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
        Authentication authentication=event.getAuthentication();
        String username=authentication.getPrincipal().toString();
        System.out.println("user  "+username+"登陆成功");
    }

    @EventListener({LogoutSuccessEvent.class})
    @Async
    public void onLogoutSuccessEvent(LogoutSuccessEvent event) {
        Authentication authentication=event.getAuthentication();
        String username=authentication.getPrincipal().toString();
        System.out.println("user  "+username+"退出成功");
    }

    @EventListener({AbstractAuthenticationFailureEvent.class})
    @Async
    public void onAbstractAuthenticationFailureEvent(AbstractAuthenticationFailureEvent event) {
        Authentication authentication=event.getAuthentication();
        if (authentication!=null) {
            //String username=authentication.getPrincipal().toString();
            System.out.println(authentication);
        }
        System.out.println(event.getException().getMessage());
    }
}
