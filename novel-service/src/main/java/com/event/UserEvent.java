package com.event;

import com.domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class UserEvent extends ApplicationEvent {
  public  static enum UserEventType {
      insert,
      update,
      delete
  }
  @Getter
  private User user;
  @Getter
  private UserEventType userEventType;
  public UserEvent(User user,UserEventType userEventType) {
      super(user);
      this.user=user;
      this.userEventType=userEventType;
  }
}
