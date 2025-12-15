package com.listeners;

import com.domain.User;
import com.event.UserEvent;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component//通过使用这一注解，开发者可以将普通的Java类转换为Spring容器中的一个Bean，从而实现依赖注入和生命周期管理。
public class MailListener {
    @EventListener(value = {UserEvent.class},condition="#root.event().userEventType==T(com.event.UserEvent.UserEventType).insert")
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserEvent(UserEvent userEvent) throws InterruptedException {
        User user= userEvent.getUser();
        System.out.println("发送邮件"+user.getUsername());
        Thread.sleep(7000L);
        System.out.println("邮件发送完毕"+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}