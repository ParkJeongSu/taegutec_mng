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
@Table(name = "CARRIERS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class CarriersEntity {
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

    @Column(name = "SHELF_NAME")
    private String shelfName;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "CLEAN_STATE")
    private String cleanState;

    @Column(name = "TRANSPORT_STATE")
    private String transportState;

    @Column(name = "RESERVED_OBJECT_ID")
    private String reservedObjectId;

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

    @Column(name = "LOT_QUANTITY")
    private Integer lotQuantity;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "CAPA_STATE")
    private String capaState;

    @Column(name = "LAST_CLEAN_TIME")
    private LocalDateTime lastCleanTime;

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

    @Column(name = "CONTAINER_TYPE")
    private String containerType;
}
