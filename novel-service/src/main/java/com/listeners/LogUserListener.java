package com.listeners;

import com.domain.User;
import com.event.UserEvent;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LogUserListener {
    @EventListener
    public void onUserEvent(UserEvent userEvent) throws InterruptedException {
       User user= userEvent.getUser();
        switch (userEvent.getUserEventType()){
            case update :
                System.out.println("修改用户"+user.getUsername());
                break;
            case insert:
                System.out.println("新增用户"+user.getUsername());
                Thread.sleep(1000L);
                System.out.println(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                break;
            case delete:
                System.out.println("删除用户");
                break;
        }
    }
}
