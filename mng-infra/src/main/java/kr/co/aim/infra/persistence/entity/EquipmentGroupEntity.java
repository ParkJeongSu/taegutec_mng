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
@Table(name = "EQUIPMENT_GROUP_DEF", catalog = "NEXBEWCS", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentGroupEntity {
    @Id
    @Column(name = "EQUIPMENTGROUPNAME")
    private String equipmentGroupName;

    @Column(name = "DESCRIPTION")
    private String description;

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
