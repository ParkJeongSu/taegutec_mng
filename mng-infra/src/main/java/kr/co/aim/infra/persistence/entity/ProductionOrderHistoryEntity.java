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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "PRODUCTION_ORDER_HISTORY", catalog = "NEXBEMNG", schema = "dbo")
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

    @Column(name = "IDOC_ID")
    private Long idocId;

    @Column(name = "H2_ORDER_DP_LINE_ID")
    private Long h2OrderDpLineId;

    @Column(name = "GAL_KEY")
    private String galKey;

    @Column(name = "MNG_KEY")
    private Long mngKey;

    @Column(name = "PRODUCTION_ORDER_TYPE")
    private String productionOrderType;

    @Column(name = "PRODUCTION_ORDER_STATE")
    private String productionOrderState;

    @Column(name = "REPORT_STATE")
    private String reportState;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PLAN_QUANTITY")
    private BigDecimal planQuantity;

    @Column(name = "RELEASED_QUANTITY")
    private BigDecimal releasedQuantity;

    @Column(name = "STARTED_QUANTITY")
    private BigDecimal startedQuantity;

    @Column(name = "ENDED_QUANTITY")
    private BigDecimal endedQuantity;

    @Column(name = "SCRAPPED_QUANTITY")
    private BigDecimal scrappedQuantity;

    @Column(name = "MATERIAL_LOT_NAME")
    private String materialLotName;

    @Column(name = "GAL_ORDER_ID")
    private String galOrderId;

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
