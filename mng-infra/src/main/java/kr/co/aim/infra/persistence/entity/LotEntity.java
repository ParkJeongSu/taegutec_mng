package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "LOT", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LotEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOT_NAME")
    private String lotName;

    @Column(name = "ORIGINAL_LOT_NAME")
    private String originalLotName;

    @Column(name = "LOT_STATUS")
    private String lotStatus;

    @Column(name = "ITEM_ID")
    private String itemId;

    @Column(name = "TOTAL_QUANTITY")
    private BigDecimal totalQuantity;

    @Column(name = "HOLD_STATE")
    private String holdState;

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
}