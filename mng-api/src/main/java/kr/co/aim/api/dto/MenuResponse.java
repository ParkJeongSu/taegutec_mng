package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.infra.persistence.entity.MenuEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 조회 응답 DTO")
public class MenuResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "다국어 식별자", example = "MENU_USER_MNG")
    private String menuId;

    @Schema(description = "메뉴 기본 표시 이름", example = "사용자 관리")
    private String menuName;

    @Schema(description = "부모 메뉴 ID (TSID)", example = "877810665130787000")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @Schema(description = "메뉴 계층 레벨", example = "1")
    private Integer menuLevel;

    @Schema(description = "Vue 파일 컴포넌트 경로", example = "/views/Settings/UserMgmtView.vue")
    private String filePath;

    @Schema(description = "브라우저 라우터 경로", example = "/settings/user-mgmt")
    private String routerPath;

    @Schema(description = "아이콘 명칭", example = "UserOutlined")
    private String iconName;

    @Schema(description = "정렬 순서", example = "10")
    private Integer displayOrder;

    @Schema(description = "메뉴 표시 여부 (Y/N)", example = "Y")
    private String isVisible;

    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;

    @Schema(description = "이벤트 이름", example = "MenuCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-22T09:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial Menu")
    private String eventComment;

    public static MenuResponse fromEntity(MenuEntity entity) {
        if (entity == null) {
            return null;
        }
        return MenuResponse.builder()
                .id(entity.getId())
                .factoryName(entity.getFactoryName())
                .menuId(entity.getMenuId())
                .menuName(entity.getMenuName())
                .parentId(entity.getParentId())
                .menuLevel(entity.getMenuLevel())
                .filePath(entity.getFilePath())
                .routerPath(entity.getRouterPath())
                .iconName(entity.getIconName())
                .displayOrder(entity.getDisplayOrder())
                .isVisible(entity.getIsVisible())
                .useState(entity.getUseState())
                .eventName(entity.getEventName())
                .eventTime(entity.getEventTime())
                .eventUser(entity.getEventUser())
                .eventComment(entity.getEventComment())
                .build();
    }

    public static MenuResponse fromDomain(Menu domain) {
        if (domain == null) {
            return null;
        }
        return MenuResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .menuId(domain.getMenuId())
                .menuName(domain.getMenuName())
                .parentId(domain.getParentId())
                .menuLevel(domain.getMenuLevel())
                .filePath(domain.getFilePath())
                .routerPath(domain.getRouterPath())
                .iconName(domain.getIconName())
                .displayOrder(domain.getDisplayOrder())
                .isVisible(domain.getIsVisible())
                .useState(domain.getUseState())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
