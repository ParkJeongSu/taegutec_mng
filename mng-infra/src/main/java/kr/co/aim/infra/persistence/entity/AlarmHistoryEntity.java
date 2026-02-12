package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "ALARM_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@Builder
public class AlarmHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ALARM_DEF_ID")
    private Long alarmDefId;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "ALARM_STATE")
    private String alarmState;

    @Column(name = "CREATE_TIME", updatable = false) // 등록일은 수정 불가하게 설정
    private LocalDateTime createTime;

    @Column(name = "CLEAR_TIME")
    private LocalDateTime clearTime;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
