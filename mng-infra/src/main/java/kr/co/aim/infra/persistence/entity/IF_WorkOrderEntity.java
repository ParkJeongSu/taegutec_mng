package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "IF_WORK_ORDER")
@NoArgsConstructor
public class IF_WorkOrderEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "WORK_ORDER_NAME")
    private String workOrderName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "VENDOR_ID")
    private String vendorId;

    @Column(name = "PRODUCT_DEF_ID")
    private String productDefId;

    @Column(name = "PROCESSFLOW_ID")
    private String processFlowId;

    @Column(name = "PROCESSOPERATION_ID")
    private String processOperationId;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PLAN_QUANTITY")
    private Integer planQuantity;

    @Column(name = "IF_STATE")
    private String ifState;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

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
