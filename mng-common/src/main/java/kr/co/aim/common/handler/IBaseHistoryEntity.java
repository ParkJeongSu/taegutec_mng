package kr.co.aim.common.handler;

import java.time.LocalDateTime;
public interface IBaseHistoryEntity {
    Long getId();
    String getEventName();
    LocalDateTime getEventTime();
    String getEventUser();
    String getEventComment();
}
