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
@Table(name = "W_TRANSFER_COMMAND_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsTransferCommandHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "transferCommandName")
    private String transferCommandName;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "commandStatus")
    private String commandStatus;

    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "currentEquipmentName")
    private String currentEquipmentName;

    @Column(name = "hotLot")
    private String hotLot;

    @Column(name = "jobCompletedTime")
    private LocalDateTime jobCompletedTime;

    @Column(name = "jobReceiveTime")
    private LocalDateTime jobReceiveTime;

    @Column(name = "jobStartTime")
    private LocalDateTime jobStartTime;

    @Column(name = "lotName")
    private String lotName;

    @Column(name = "owner")
    private String owner;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "productQuantity")
    private Integer productQuantity;

    @Column(name = "[source]")
    private String source;

    @Column(name = "target")
    private String target;

    @Column(name = "targetEquipmentName")
    private String targetEquipmentName;

    @Column(name = "currentSource")
    private String currentSource;

    @Column(name = "orderType")
    private String orderType;

    @Column(name = "sourceEquipmentName")
    private String sourceEquipmentName;

    @Column(name = "sourceTransferType")
    private String sourceTransferType;

    @Column(name = "targetTransferType")
    private String targetTransferType;

    @Column(name = "subCommandJobNo")
    private Integer subCommandJobNo;

    @Column(name = "subCommandStatus")
    private String subCommandStatus;

    @Column(name = "transferSpeed")
    private Integer transferSpeed;

    @Column(name = "processType")
    private String processType;

    @Column(name = "startReportFlag")
    private Boolean startReportFlag;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
