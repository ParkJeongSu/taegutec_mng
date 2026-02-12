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
@Table(name = "TRANSPORT_JOB")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class TransportJobEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TRANSPORT_JOB_NAME")
    private String transportJobName;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "TRANSPORT_JOB_STATE")
    private String transportJobState;

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

    @Column(name = "PRIORITY")
    private Integer priority;

    @Column(name = "ERROR_CODE")
    private String errorCode;

    @Column(name = "ERROR_TEXT")
    private String errorText;

    @Column(name = "REQUEST_TYPE")
    private String requestType;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    @Column(name = "DEPARTED_TIME")
    private LocalDateTime departedTime;

    @Column(name = "ARRIVED_TIME")
    private LocalDateTime arrivedTime;

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

    @Column(name = "TASK_JOB_ID")
    private Long taskJobId;
}
