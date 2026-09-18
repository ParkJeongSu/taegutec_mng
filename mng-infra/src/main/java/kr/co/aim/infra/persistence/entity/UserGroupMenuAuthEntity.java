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
@Table(name = "USER_GROUP_MENU_AUTH", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroupMenuAuthEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "USER_GROUP_ID")
    private Long userGroupId;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "AUTH_LEVEL")
    private String authLevel;

    @Column(name = "READABLE")
    private String readable;

    @Column(name = "WRITABLE")
    private String writable;

    @Column(name = "DESCRIPTION")
    private String description;

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
}
