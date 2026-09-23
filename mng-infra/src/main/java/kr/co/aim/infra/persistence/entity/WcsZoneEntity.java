package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "[ZONE]", catalog = "NEXBEWCS", schema = "dbo")
@IdClass(WcsZoneId.class)
public class WcsZoneEntity {

    @Id
    @Column(name = "factoryName")
    private String factoryName;

    @Id
    @Column(name = "zoneName")
    private String zoneName;

    @Column(name = "deepFirstFlag")
    private Boolean deepFirstFlag;

    @Column(name = "frontRowInterval")
    private Integer frontRowInterval;

    @Column(name = "loadType")
    private String loadType;

    @Column(name = "maxCapacityPercent", precision = 5, scale = 2)
    private BigDecimal maxCapacityPercent;

    @Column(name = "zoneCapacity")
    private Integer zoneCapacity;

    @Column(name = "zoneColor")
    private String zoneColor;

    @Column(name = "zoneSize")
    private Integer zoneSize;

    @Column(name = "zoneType")
    private String zoneType;

    @Column(name = "shelfSelectMode")
    private String shelfSelectMode;

    @Column(name = "useCapacityPercent", precision = 5, scale = 2)
    private BigDecimal useCapacityPercent;

    @Column(name = "waitingAreaFlag")
    private Boolean waitingAreaFlag;

    @Column(name = "lastEventComment")
    private String lastEventComment;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;
}
