package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "ALARM_ACTION_USER_GROUP_USERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class AlarmActionUserGroupUsersEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ALARM_ACTION_USER_GROUP_ID")
    private Long alarmActionUserGroupId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
