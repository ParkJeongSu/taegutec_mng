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
@Table(name = "MENU", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "MENU_ID")
    private String menuId; // 다국어 식별자

    @Column(name = "MENU_NAME")
    private String menuName; // 기본 표시 이름

    @Column(name = "PARENT_ID")
    private Long parentId; // 부모 메뉴 id

    @Column(name = "MENU_LEVEL")
    private Integer menuLevel;

    @Column(name = "FILE_PATH")
    private String filePath; // 실제 vue 파일 위치

    @Column(name = "ROUTER_PATH")
    private String routerPath; // 브라우저의 URL 경로

    @Column(name = "ICON_NAME")
    private String iconName;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "IS_VISIBLE")
    private String isVisible;

    @Column(name = "USE_STATE")
    private String useState;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
