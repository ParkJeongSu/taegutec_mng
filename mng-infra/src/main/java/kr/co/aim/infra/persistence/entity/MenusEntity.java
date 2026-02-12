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
@Table(name = "MENUS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class MenusEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "SYSTEM_DEF_ID")
    private Long systemDefId;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "PARENT_MENU_ID")
    private Long parentMenuId;

    @Column(name = "VIEW_URL")
    private String viewURL;

    @Column(name = "MENU_SEQ")
    private Integer menuSEQ;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ICON_NAME")
    private String iconName;

    @Column(name = "MENU_TYPE")
    private String menuType;

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
