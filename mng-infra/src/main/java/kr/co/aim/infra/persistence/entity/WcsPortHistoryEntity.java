package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "W_PORT_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsPortHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "localNo")
    private Integer localNo;

    @Column(name = "portNumber")
    private Integer portNumber;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "errorHappen")
    private String errorHappen;

    @Column(name = "portContainStatus")
    private String portContainStatus;

    @Column(name = "portEnableMode")
    private String portEnableMode;

    @Column(name = "portName")
    private String portName;

    @Column(name = "portStatus")
    private String portStatus;

    @Column(name = "portTransferStatus")
    private String portTransferStatus;

    @Column(name = "portType")
    private String portType;

    @Column(name = "touchPanelNumber")
    private Integer touchPanelNumber;

    @Column(name = "zoneName")
    private String zoneName;

    @Column(name = "bin")
    private Integer bin;

    @Column(name = "[row]")
    private Integer row;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "col")
    private Integer col;

    @Column(name = "linkEquipmentName")
    private String linkEquipmentName;

    @Column(name = "linkPortName")
    private String linkPortName;

    @Column(name = "linkPortType")
    private String linkPortType;

    @Column(name = "portTransferMode")
    private String portTransferMode;

    @Column(name = "portReadingEnableMode")
    private String portReadingEnableMode;

    @Column(name = "portDetailType")
    private String portDetailType;

    @Column(name = "rejectEquipmentName")
    private String rejectEquipmentName;

    @Column(name = "rejectPortName")
    private String rejectPortName;

    @Column(name = "useWorkerFlag")
    private Boolean useWorkerFlag;

    @Column(name = "portUseType")
    private String portUseType;

    @Column(name = "portMode")
    private String portMode;

    @Column(name = "portOperationMode")
    private String portOperationMode;

    @Column(name = "portRfidEnableMode")
    private String portRfidEnableMode;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
