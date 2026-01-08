package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "LOTS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class LotsEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "PRODUCTION_TYPE")
    private String productionType;

    @Column(name = "LOT_STATE")
    private String lotState;

    @Column(name = "PROCESS_STATE")
    private String processState;

    @Column(name = "PRODUCT_DEF_ID")
    private String productDefId;

    @Column(name = "PROCESS_SPEC_ID")
    private String processSpecId;

    @Column(name = "PROCESS_SPEC_VERSION")
    private String processSpecVersion;

    @Column(name = "PROCESS_FLOW_ID")
    private String processFlowId;

    @Column(name = "PROCESS_OPERATION_ID")
    private String processOperationId;

    @Column(name = "WORK_ORDER_ID")
    private String workOrderId;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PORT_NAME")
    private String portName;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @Column(name = "CARRIER_ID")
    private Long carrierId;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "LOT_GRADE")
    private String lotGrade;

    @Column(name = "PRODUCTION_DETAIL_TYPE")
    private String productionDetailType;

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

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "OLD_QUANTITY")
    private Integer oldQuantity;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REWORK_STATE")
    private String reworkState;

    @Column(name = "REWORK_COUNT")
    private Integer reworkCount;

    @Column(name = "ORIGINAL_PROCESS_SPEC_ID")
    private String originalProcessSpecId;

    @Column(name = "ORIGINAL_PROCESS_SPEC_VERSION")
    private String originalProcessSpecVersion;

    @Column(name = "RETURN_PROCESS_FLOW_ID")
    private String returnProcessFlowId;

    @Column(name = "RETURN_PROCESS_OPERATION_ID")
    private String returnProcessOperationId;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "OWNER_CODE")
    private String ownerCode;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
