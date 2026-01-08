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
@Table(name = "TASK_JOB_DETAIL_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class TaskJobDetailHistoryEntity {
    @Id
    private Long id;

    @Column(name = "TASK_JOB_ID")
    private Long taskJobId;

    @Column(name = "WIP_NAME")
    private String wipName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "DEPARTED_TIME")
    private LocalDateTime departedTime;

    @Column(name = "ARRIVED_TIME")
    private LocalDateTime arrivedTime;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "COMPLETED_TIME")
    private LocalDateTime completedTime;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
