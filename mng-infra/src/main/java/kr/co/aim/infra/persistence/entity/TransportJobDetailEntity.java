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
@Table(name = "TRANSPORT_JOB_DETAIL")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class TransportJobDetailEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRANSPORT_JOB_DETAIL_NAME")
    private String transportJobDetailName;

    @Column(name = "TRANSPORT_JOB_ID")
    private Long transportJobId;

    @Column(name = "TRANSPORT_JOB_DETAIL_STATE")
    private String transportJobDetailState;

    @Column(name = "CARRIER_ID")
    private String carrierId;

    @Column(name = "SOURCE_EQUIPMENT_NAME")
    private String sourceEquipmentName;

    @Column(name = "SOURCE_PORT_NAME")
    private String sourcePortName;

    @Column(name = "SOURCE_ZONE_NAME")
    private String sourceZoneName;

    @Column(name = "SOURCE_SHELF_NAME")
    private String sourceShelfName;

    @Column(name = "DESTINATION_EQUIPMENT_NAME")
    private String destinationEquipmentName;

    @Column(name = "DESTINATION_PORT_NAME")
    private String destinationPortName;

    @Column(name = "DESTINATION_ZONE_NAME")
    private String destinationZoneName;

    @Column(name = "DESTINATION_SHELF_NAME")
    private String destinationShelfName;

    @Column(name = "CURRENT_EQUIPMENT_NAME")
    private String currentEquipmentName;

    @Column(name = "CURRENT_PORT_NAME")
    private String currentPortName;

    @Column(name = "CURRENT_ZONE_NAME")
    private String currentZoneName;

    @Column(name = "CURRENT_SHELF_NAME")
    private String currentShelfName;

    @Column(name = "STEP_ORDER")
    private Integer stepOrder;

    @Column(name = "STEP_PHASE")
    private Integer stepPhase;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
