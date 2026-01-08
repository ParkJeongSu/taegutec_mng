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
@Table(name = "TASK_JOB_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class TaskJobHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TASK_NAME")
    private String taskName;

    @Column(name = "TASK_TYPE")
    private String taskType;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "TASK_GROUP_NAME")
    private String taskGroupName;

    @Column(name = "STEP")
    private Integer step;

    @Column(name = "WORK_ORDER_ID")
    private Long workOrderId;

    @Column(name = "TASK_STATE")
    private String taskState;

    @Column(name = "CARRIER_COUNT")
    private Integer carrierCount;

    @Column(name = "TRANSPORT_TRY_COUNT")
    private Integer transportTryCount;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

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
