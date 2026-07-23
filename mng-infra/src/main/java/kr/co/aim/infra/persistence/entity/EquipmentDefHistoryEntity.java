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
@Table(name = "EQUIPMENT_DEF", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentDefHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EQUIPMENT_TYPE")
    private String equipmentType;

    @Column(name = "EQUIPMENT_GROUP_NAME")
    private String equipmentGroupName;

    @Column(name = "DETAIL_EQUIPMENT_TYPE")
    private String detailEquipmentType;

    @Column(name = "VENDOR_ID")
    private String vendorId;

    @Column(name = "MODEL_ID")
    private String modelId;

    @Column(name = "PROCESS_CAPACITY")
    private Integer processCapacity;

    @Column(name = "CONTAINER_TYPE")
    private String containerType;

    @Column(name = "PLC_TYPE")
    private String plcType;

    @Column(name = "ROUTE_KEY")
    private String routeKey;

    @Column(name = "SERVER_NAME")
    private String serverName;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "LOCAL_NO")
    private Integer localNo;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}