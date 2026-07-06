package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "TRANSPORT_ORDER", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransportOrderEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TRANSPORT_ORDER_ID")
    private String transportOrderId;

    @Column(name = "IDOC_ID")
    private Long idocId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "VIRTUAL_CARRIER_NAME")
    private String virtualCarrierName;

    @Column(name = "TRANSPORT_TYPE")
    private String transportType;

    @Column(name = "TRANSPORT_STATUS")
    private String transportStatus;

    @Column(name = "LAST_TRANSACTION_CODE")
    private String lastTransactionCode;

    @Column(name = "CARRIER_TYPE")
    private String carrierType;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "GAL_ID")
    private String galId; // cGalId only reply need

    @Column(name = "GAL_WAREHOUSE")
    private String galWarehouse; // cGalWarehouse only reply need

    @Column(name = "LOCATION_ID")
    private String locationId; // storage location Number 저장 위치 xxyyzz…

    @Column(name = "WORK_STATION_ID")
    private String workStationId; //Inbound : 현재위치, Outbound : 타겟의 위치 Outbound 에서 목적지로 사용

    @Column(name = "SOURCE_ZONE_NAME")
    private String sourceZoneName;

    @Column(name = "DESTINATION_ZONE_NAME")
    private String destinationZoneName;

    @Column(name = "ERROR_TEXT")
    private String errorText;

    @Column(name = "ACTUAL_WEIGHT")
    private String actualWeight;

    @Column(name = "REQUESTED_ZONE_NAME")
    private String requestedZoneName;

    @Column(name = "ACTUAL_ZONE_NAME")
    private String actualZoneName;

    @Column(name = "ACTUAL_LOCATION_ID")
    private String actualLocationId;

    @Column(name = "TRAVEL_PROFILE")
    private String travelProfile;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "RELEASE_TIME")
    private LocalDateTime releaseTime;

    @Column(name = "COMPLETE_TIME")
    private LocalDateTime completeTime;

    @Column(name = "RETRIEVAL_TIME")
    private LocalDateTime retrievalTime;

    @Column(name = "CREATE_USER")
    private String createUser;

    @Column(name = "RELEASE_USER")
    private String releaseUser;

    @Column(name = "COMPLETE_USER")
    private String completeUser;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}