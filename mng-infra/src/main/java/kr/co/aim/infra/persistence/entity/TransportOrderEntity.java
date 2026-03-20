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
@Table(name = "TRANSPORT_ORDER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransportOrderEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "TRANSPORT_ORDER_NAME", nullable = false)
    private String transportOrderName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRANSPORT_TYPE")
    private String transportType;

    @Column(name = "TRANSPORT_ORDER_ID")
    private String transportOrderId;

    @Column(name = "TRANSPORT_STATUS")
    private String transportStatus;

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "GAL_ID")
    private String galId;

    @Column(name = "GAL_WAREHOUSE")
    private String galWarehouse;

    @Column(name = "SOURCE_WORK_CENTER")
    private String sourceWorkCenter;

    @Column(name = "SOURCE_EQUIPMENT_NAME")
    private String sourceEquipmentName;

    @Column(name = "SOURCE_PORT_NAME")
    private String sourcePortName;

    @Column(name = "SOURCE_ZONE_NAME")
    private String sourceZoneName;

    @Column(name = "SOURCE_POSITION_TYPE_NAME")
    private String sourcePositionTypeName;

    @Column(name = "SOURCE_POSITION_NAME")
    private String sourcePositionName;

    @Column(name = "DESTINATION_WORK_CENTER")
    private String destinationWorkCenter;

    @Column(name = "DESTINATION_EQUIPMENT_NAME")
    private String destinationEquipmentName;

    @Column(name = "DESTINATION_PORT_NAME")
    private String destinationPortName;

    @Column(name = "DESTINATION_ZONE_NAME")
    private String destinationZoneName;

    @Column(name = "DESTINATION_POSITION_TYPE_NAME")
    private String destinationPositionTypeName;

    @Column(name = "DESTINATION_POSITION_NAME")
    private String destinationPositionName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "CARRIER_TYPE")
    private String carrierType;

    @Column(name = "DRIVING_PROFILE")
    private String drivingProfile;

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

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}