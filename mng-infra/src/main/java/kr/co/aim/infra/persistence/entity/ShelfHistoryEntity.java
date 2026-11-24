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

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "SHELF_HISTORY", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShelfHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "LOCATION_ID")
    private String locationId;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "ZONE_ID")
    private Long zoneId;

    @Column(name = "AREA_ID")
    private Long areaId;

    @Column(name = "CAPACITY")
    private Integer capacity;

    @Column(name = "SHELF_STATE")
    private String shelfState;

    @Column(name = "COMMUNICATION_STATE")
    private String communicationState;

    @Column(name = "HOLD_STATE")
    private String holdState;

    @Column(name = "LENGTH")
    private Integer length;

    @Column(name = "WIDTH")
    private Integer width;

    @Column(name = "HEIGHT")
    private Integer height;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
