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
@Table(name = "WORK_ORDER_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class WorkOrderHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "WORK_ORDER_NAME")
    private String workOrderName;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "VENDOR_NAME")
    private String vendorName;

    @Column(name = "PRODUCT_DEF_NAME")
    private String productDefName;

    @Column(name = "PROCESS_FLOW_NAME")
    private String processFlowName;

    @Column(name = "PROCESS_OPERATION_NAME")
    private String processOperationName;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @Column(name = "WORK_ORDER_STATE")
    private String workOrderState;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PLAN_QUANTITY")
    private Integer planQuantity;

    @Column(name = "CREATED_QUANTITY")
    private Integer createdQuantity;

    @Column(name = "RELEASED_QUANTITY")
    private Integer releasedQuantity;

    @Column(name = "FINISHED_QUANTITY")
    private Integer finishedQuantity;

    @Column(name = "SCRAPPED_QUANTITY")
    private Integer scrappedQuantity;

    @Column(name = "WORK_ORDER_COUNT")
    private Integer workOrderCount;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Column(name = "COMPLETE_TIME")
    private LocalDateTime completeTime;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "RELEASE_USER")
    private String releaseUser;

    @Column(name = "COMPLETE_USER")
    private String completeUser;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
