package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "PRODUCTION_ORDER_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
@NoArgsConstructor
public class ProductionOrderHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "ORDER_LINE_NUMBER")
    private String orderLineNumber;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "GAL_ID")
    private String galId;

    @Column(name = "PRODUCTION_ORDER_TYPE")
    private String productionOrderType;

    @Column(name = "PRODUCTION_ORDER_STATE")
    private String productionOrderState;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PLAN_QUANTITY")
    private Integer planQuantity;

    @Column(name = "RELEASED_QUANTITY")
    private Integer releasedQuantity;

    @Column(name = "STARTED_QUANTITY")
    private Integer startedQuantity;

    @Column(name = "ENDED_QUANTITY")
    private Integer endedQuantity;

    @Column(name = "SCRAPPED_QUANTITY")
    private Integer scrappedQuantity;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Column(name = "COMPLETE_TIME")
    private LocalDateTime completeTime;

    @Column(name = "VALIDATION_TIME")
    private LocalDateTime validationTime;

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
