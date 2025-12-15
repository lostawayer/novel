package com.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserTask {
    @Scheduled(cron = "${medical.com.service.userTask.cron}")//使其按指定时间间隔或时间表执行
    @Async
    public void userTask(){
        //System.out.println("userTask");
        throw new RuntimeException("userTask wrang");
    }
}
