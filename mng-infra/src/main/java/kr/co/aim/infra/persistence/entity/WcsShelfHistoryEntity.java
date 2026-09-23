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
@Table(name = "SHELF_HISTORY", catalog = "NEXBEWCSHT", schema = "dbo")
public class WcsShelfHistoryEntity {

    @Id
    @Column(name = "eventTimeKey", length = 30)
    private String eventTimeKey;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "shelfName")
    private String shelfName;

    @Column(name = "stockerName")
    private String stockerName;

    @Column(name = "abnormalStageNumber")
    private Integer abnormalStageNumber;

    @Column(name = "bin")
    private Integer bin;

    @Column(name = "carrierName")
    private String carrierName;

    @Column(name = "col")
    private Integer col;

    @Column(name = "lastTransferCmdName")
    private String lastTransferCmdName;

    @Column(name = "numberOfUses")
    private Integer numberOfUses;

    @Column(name = "[row]")
    private Integer row;

    @Column(name = "shelfEnableMode")
    private String shelfEnableMode;

    @Column(name = "shelfStatus")
    private String shelfStatus;

    @Column(name = "shelfTransferStatus")
    private String shelfTransferStatus;

    @Column(name = "shelfType")
    private String shelfType;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "zoneName")
    private String zoneName;

    @Column(name = "eventComment")
    private String eventComment;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "eventTime")
    private LocalDateTime eventTime;

    @Column(name = "eventUser")
    private String eventUser;
}
