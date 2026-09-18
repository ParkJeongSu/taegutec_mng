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
@Table(name = "MENU_HISTORY", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "PARENT_MENU_ID")
    private Long parentMenuId;

    @Column(name = "MENU_LEVEL")
    private Integer menuLevel;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "VIEW_URL")
    private String viewUrl;

    @Column(name = "ICON_NAME")
    private String iconName;

    @Column(name = "DESCRIPTION")
    private String description;

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
