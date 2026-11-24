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
@Table(name = "INSERT_AUTO_RELOCATION_TEST", catalog = "NEXBEMNG", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InsertAutoRelocationTestEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "WAREHOUSE_NAME1")
    private String wareHouseName1;

    @Column(name = "WAREHOUSE_NAME2")
    private String wareHouseName2;

    @Column(name = "ZONE_NAME1")
    private String zoneName1;

    @Column(name = "ZONE_NAME2")
    private String zoneName2;

    @Column(name = "CARRIER_NAME")
    private String carrierName;

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
