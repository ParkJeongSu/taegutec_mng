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
@Table(name = "WIP")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class WipEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "WIP_NAME")
    private String wipName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "WORK_ORDER_NAME")
    private String workOrderName;

    @Column(name = "PRODUCT_DEF_NAME")
    private String productDefName;

    @Column(name = "PROCESS_FLOW_NAME")
    private String processFlowName;

    @Column(name = "PROCESS_OPERATION_NAME")
    private String processOperationName;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "WIP_STATE")
    private String wipState;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PORT_NAME")
    private String portName;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @Column(name = "WIP_GRADE")
    private String wipGrade;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "PLAN_START_DATE")
    private LocalDateTime planStartDate;

    @Column(name = "PLAN_DUE_DATE")
    private LocalDateTime planDueDate;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Column(name = "SHIP_TIME")
    private LocalDateTime shipTime;

    @Column(name = "TRACK_IN_TIME")
    private LocalDateTime trackInTime;

    @Column(name = "TRACK_OUT_TIME")
    private LocalDateTime trackOutTime;

    @Column(name = "OPERATION_MOVE_TIME")
    private LocalDateTime operationMoveTime;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
