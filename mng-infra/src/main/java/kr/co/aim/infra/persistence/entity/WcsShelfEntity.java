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
@Table(name = "SHELF", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsShelfId.class)
public class WcsShelfEntity {

    @Id
    @Column(name = "factoryName", length = 32, nullable = false)
    private String factoryName;

    @Id
    @Column(name = "shelfName", length = 64, nullable = false)
    private String shelfName;

    @Id
    @Column(name = "stockerName", length = 64, nullable = false)
    private String stockerName;

    @Column(name = "abnormalStageNumber", nullable = false)
    @Builder.Default
    private Integer abnormalStageNumber = 0;

    @Column(name = "bin", nullable = false)
    @Builder.Default
    private Integer bin = 0;

    @Column(name = "carrierName", length = 64)
    private String carrierName;

    @Column(name = "col", nullable = false)
    @Builder.Default
    private Integer col = 0;

    @Column(name = "lastTransferCmdName", length = 64)
    private String lastTransferCmdName;

    @Column(name = "numberOfUses", nullable = false)
    @Builder.Default
    private Integer numberOfUses = 0;

    @Column(name = "[row]", nullable = false)
    @Builder.Default
    private Integer row = 0;

    @Column(name = "shelfEnableMode", length = 20)
    private String shelfEnableMode;

    @Column(name = "shelfStatus", length = 20)
    private String shelfStatus;

    @Column(name = "shelfTransferStatus", length = 20)
    private String shelfTransferStatus;

    @Column(name = "shelfType", length = 20)
    private String shelfType;

    @Column(name = "stage", nullable = false)
    @Builder.Default
    private Integer stage = 0;

    @Column(name = "zoneName", length = 64)
    private String zoneName;

    @Column(name = "lastEventComment", length = 255)
    private String lastEventComment;

    @Column(name = "lastEventName", length = 64)
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser", length = 64)
    private String lastEventUser;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.abnormalStageNumber == null) this.abnormalStageNumber = 0;
        if (this.bin == null) this.bin = 0;
        if (this.col == null) this.col = 0;
        if (this.numberOfUses == null) this.numberOfUses = 0;
        if (this.row == null) this.row = 0;
        if (this.stage == null) this.stage = 0;
    }
}