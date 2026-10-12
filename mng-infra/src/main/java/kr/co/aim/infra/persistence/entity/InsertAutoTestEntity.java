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
@Table(name = "INSERT_AUTO_TEST", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsertAutoTestEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "WAREHOUSE_NAME")
    private String wareHouseName;

    @Column(name = "WORK_STATION_ID")
    private String workStationId;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

    @Column(name = "ZONE_NAME")
    private String zoneName;

    @Column(name = "INBOUND_PORT_NAME")
    private String inboundPortName;

    @Column(name = "TRAVEL_PROFILE")
    private String travelProfile;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
