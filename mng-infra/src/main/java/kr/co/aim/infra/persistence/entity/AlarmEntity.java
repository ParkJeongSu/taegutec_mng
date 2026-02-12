package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "ALARM")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class AlarmEntity {
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
    // 저장하기 직전에 이 메서드가 자동으로 실행됨!
    @PrePersist
    public void onPrePersist() {

        // 2. 등록일시가 없으면 현재 시간 주입
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }

    }
}
