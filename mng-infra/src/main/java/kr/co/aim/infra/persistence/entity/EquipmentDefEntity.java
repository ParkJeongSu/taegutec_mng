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
@Table(name = "EQUIPMENT_DEF")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentDefEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "EQUIPMENT_DEF_NAME")
    private String equipmentDefName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EQUIPMENT_TYPE")
    private String equipmentType;

    @Column(name = "EQUIPMENT_GROUP_ID")
    private Long equipmentGroupId;

    @Column(name = "DETAIL_EQUIPMENT_TYPE")
    private String detailEquipmentType;

    @Column(name = "VENDOR_ID")
    private String vendorId;

    @Column(name = "MODEL_ID")
    private String modelId;

    @Column(name = "PROCESS_CAPACITY")
    private Integer processCapacity;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;

    @Column(name = "CONTAINER_TYPE")
    private String containerType;
}
