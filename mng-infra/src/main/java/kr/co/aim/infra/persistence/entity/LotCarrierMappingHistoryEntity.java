package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "LOT_CARRIER_MAPPING_HISTORY", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LotCarrierMappingHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "ORDER_LINE_NUMBER")
    private String orderLineNumber;

    @Column(name = "PROCESS_STATUS")
    private String processStatus;

    @Column(name = "QUANTITY")
    private BigDecimal quantity;

    @Column(name = "GAL_QUANTITY")
    private BigDecimal galQuantity;

    @Column(name = "MNG_KEY")
    private Long mngKey;

    @Column(name = "JOB_START_TIME")
    private LocalDateTime jobStartTime;

    @Column(name = "JOB_END_TIME")
    private LocalDateTime jobEndTime;

    @Column(name = "MANTI_REQUEST_STATE")
    private String mantiRequestState;

    @Column(name = "MANTI_REQUEST_TIME")
    private LocalDateTime mantiRequestTime;

    @Column(name = "MANTI_REPLY_TIME")
    private LocalDateTime mantiReplyTime;

    @Column(name = "RRN_REQUEST_STATE")
    private String rrnRequestState;

    @Column(name = "RRN_REQUEST_TIME")
    private LocalDateTime rrnRequestTime;

    @Column(name = "RRN_REPLY_TIME")
    private LocalDateTime rrnReplyTime;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}