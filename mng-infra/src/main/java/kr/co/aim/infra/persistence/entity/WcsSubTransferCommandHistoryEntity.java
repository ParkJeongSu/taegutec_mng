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
@Table(name = "W_SUB_TRANSFER_COMMAND_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsSubTransferCommandHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "jobNo")
    private Integer jobNo;

    @Column(name = "transferCommandName")
    private String transferCommandName;

    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "currentCommandData")
    private String currentCommandData;

    @Column(name = "jobCompleteState")
    private String jobCompleteState;

    @Column(name = "localNo")
    private Integer localNo;

    @Column(name = "sourcePositionColumn")
    private String sourcePositionColumn;

    @Column(name = "sourcePositionName")
    private String sourcePositionName;

    @Column(name = "sourcePositionPortNo")
    private String sourcePositionPortNo;

    @Column(name = "sourcePositionRow")
    private String sourcePositionRow;

    @Column(name = "sourcePositionStage")
    private String sourcePositionStage;

    @Column(name = "sourcePositionBin")
    private String sourcePositionBin;

    @Column(name = "subCommandStatus")
    private String subCommandStatus;

    @Column(name = "targetPositionColumn")
    private String targetPositionColumn;

    @Column(name = "targetPositionName")
    private String targetPositionName;

    @Column(name = "targetPositionPortNo")
    private String targetPositionPortNo;

    @Column(name = "targetPositionRow")
    private String targetPositionRow;

    @Column(name = "targetPositionStage")
    private String targetPositionStage;

    @Column(name = "targetPositionBin")
    private String targetPositionBin;

    @Column(name = "transferEquipmentName")
    private String transferEquipmentName;

    @Column(name = "transferType")
    private String transferType;

    @Column(name = "transferUnitName")
    private String transferUnitName;

    @Column(name = "transferUnitNumber")
    private Integer transferUnitNumber;

    @Column(name = "endTime")
    private LocalDateTime endTime;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "transferSpeed")
    private Integer transferSpeed;

    @Column(name = "loadType")
    private String loadType;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
