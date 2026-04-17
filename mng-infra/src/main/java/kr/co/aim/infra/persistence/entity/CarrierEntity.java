package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "CARRIER", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class CarrierEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "CARRIER_DEF_ID")
    private Long carrierDefId;

    @Column(name = "CARRIER_STATE")
    private String carrierState;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "PORT_NAME")
    private String portName;

    @Column(name = "ZONE_NAME")
    private String zoneName;

    @Column(name = "POSITION_TYPE_NAME")
    private String positionTypeName;

    @Column(name = "POSITION_NAME")
    private String positionName;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "CLEAN_STATE")
    private String cleanState;

    @Column(name = "TRANSPORT_STATE")
    private String transportState;

    @Column(name = "TRANSPORT_JOB_ID")
    private String transportJobId;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "REASON_CODE")
    private String reasonCode;

    @Column(name = "USE_STATE")
    private String useState;

    @Column(name = "USE_COUNT")
    private Integer useCount;

    @Column(name = "USE_COUNT_PER_CLEAN")
    private Integer useCountPerClean;

    @Column(name = "CLEAN_COUNT")
    private Integer cleanCount;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "LOT_STATUS")
    private String lotStatus;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "ORDER_LINE_NUMBER")
    private String orderLineNumber;

    @Column(name = "ITEM_ID")
    private String itemId;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "LAST_IDOC_ID")
    private String lastIdocId;

    @Column(name = "INTERFACE_STATUS")
    private String interfaceStatus;

    @Column(name = "INTERFACE_REQUEST_TIME")
    private LocalDateTime interfaceRequestTime;

    @Column(name = "INTERFACE_REPLY_TIME")
    private LocalDateTime interfaceReplyTime;

    @Column(name = "EQUIPMENT_FLAG")
    private String equipmentFlag;

    @Column(name = "JOB_END_TIME")
    private LocalDateTime jobEndTime;

    @Column(name = "LAST_CLEAN_TIME")
    private LocalDateTime lastCleanTime;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "INBOUND_TIME")
    private LocalDateTime inboundTime;

    @Column(name = "OUTBOUND_TIME")
    private LocalDateTime outboundTime;

    @Column(name = "CONTAINER_TYPE")
    private String containerType;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

}
