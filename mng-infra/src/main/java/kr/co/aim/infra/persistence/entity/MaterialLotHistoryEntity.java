package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "MATERIAL_LOT_HISTORY", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialLotHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "MATERIAL_LOT_ID")
    private String materialLotId;

    @Column(name = "MATERIAL_DEF_ID")
    private Long materialDefId;

    @Column(name = "PARENT_MATERIAL_LOT_ID")
    private Long parentMaterialLotId;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "QUANTITY")
    private BigDecimal quantity;

    @Column(name = "STATE")
    private String state;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
