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
@Table(name = "EQUIPMENT_GROUP_DEF", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class EquipmentGroupDefEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "EQUIPMENT_GROUP_NAME", length = 40, nullable = false)
    private String equipmentGroupName;

    @Column(name = "DESCRIPTION", length = 400)
    private String description;

    @Column(name = "CHECK_OUT_STATE", length = 40)
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER", length = 40)
    private String checkOutUser;

    @Column(name = "DATA_STATE", length = 40)
    private String dataState;

    @Column(name = "EVENT_NAME", length = 40)
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER", length = 40)
    private String eventUser;

    @Column(name = "EVENT_COMMENT", length = 100)
    private String eventComment;
}