package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "ALARM_ACTION_USER_GROUP_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class AlarmActionUserGroupHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "USER_GROUP_NAME")
    private String userGroupName;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
