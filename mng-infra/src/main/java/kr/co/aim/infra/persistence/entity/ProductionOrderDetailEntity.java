package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "PRODUCTION_ORDER_DETAIL", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionOrderDetailEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRODUCTION_ORDER_ID")
    private Long productionOrderId;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "ORDER_LINE_NUMBER")
    private String orderLineNumber;

    @Column(name = "SEQ")
    private Integer seq;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "JOB_STATE")
    private String jobState;

    @Column(name = "ALLOCATED_QUANTITY")
    private BigDecimal allocatedQuantity;

    @Column(name = "ACTUAL_QUANTITY")
    private BigDecimal actualQuantity;

    @Column(name = "SEND_TIME")
    private LocalDateTime sendTime;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "COMPLETE_TIME")
    private LocalDateTime completeTime;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}