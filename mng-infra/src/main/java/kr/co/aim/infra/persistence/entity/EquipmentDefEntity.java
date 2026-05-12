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
@Table(name = "EQUIPMENT_DEF", catalog = "NEXBEWCS", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentDefEntity {

    @Id
    @Column(name = "equipmentName")
    private String equipmentName;

    @Column(name = "factoryName")
    private String factoryName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "equipmentType")
    private String equipmentType;

    @Column(name = "equipmentGroupName")
    private String equipmentGroupName;

    @Column(name = "detailEquipmentType")
    private String detailEquipmentType;

    @Column(name = "vendorId")
    private String vendorId;

    @Column(name = "modelId")
    private String modelId;

    @Column(name = "processCapacity")
    private Integer processCapacity;

    @Column(name = "containerType")
    private String containerType;

    @Column(name = "checkoutState")
    private String checkOutState;

    @Column(name = "checkoutTime")
    private LocalDateTime checkOutTime;

    @Column(name = "checkoutUser")
    private String checkOutUser;

    @Column(name = "dataState")
    private String dataState;

    @Column(name = "lastEventName")
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser")
    private String lastEventUser;

    @Column(name = "lastEventComment")
    private String lastEventComment;


}
