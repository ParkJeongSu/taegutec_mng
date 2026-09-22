package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.model.UserGroupMenuAuth;
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
@Schema(description = "사용자 그룹별 메뉴 권한 응답 DTO")
public class UserGroupMenuAuthResponse {

    @Schema(description = "권한 고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;

    @Schema(description = "사용자 그룹 고유 ID (USER_GROUP.ID 참조키)", example = "877810665130787000")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userGroupId;

    @Schema(description = "메뉴 고유 ID (MENU.ID 참조키)", example = "877810665130787100")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long menuId;

    @Schema(description = "메뉴 식별자 코드 (MENU.MENU_ID)", example = "MENU_USER_MNG")
    private String menuCode;

    @Schema(description = "메뉴명 (MENU.MENU_NAME)", example = "사용자 관리")
    private String menuName;

    @Schema(description = "상위 메뉴 ID (MENU.PARENT_ID)", example = "877810665130787000")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @Schema(description = "메뉴 레벨 (MENU.MENU_LEVEL)", example = "2")
    private Integer menuLevel;

    @Schema(description = "정렬 순서 (MENU.DISPLAY_ORDER)", example = "10")
    private Integer displayOrder;

    @Schema(description = "화면 파일 경로 (MENU.FILE_PATH)", example = "views/system/UserMng.vue")
    private String filePath;

    @Schema(description = "라우터 경로 (MENU.ROUTER_PATH)", example = "/system/user-mng")
    private String routerPath;

    @Schema(description = "조회 권한 여부 (Y/N)", example = "Y")
    private String authSelect;

    @Schema(description = "등록/수정 권한 여부 (Y/N)", example = "Y")
    private String authSave;

    @Schema(description = "삭제 권한 여부 (Y/N)", example = "N")
    private String authDelete;

    @Schema(description = "이벤트 이름", example = "UserGroupMenuAuthCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-09-22T13:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Initial menu auth assignment")
    private String eventComment;

    public static UserGroupMenuAuthResponse fromDomain(UserGroupMenuAuth domain) {
        if (domain == null) {
            return null;
        }
        return UserGroupMenuAuthResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .userGroupId(domain.getUserGroupId())
                .menuId(domain.getMenuId())
                .authSelect(domain.getAuthSelect())
                .authSave(domain.getAuthSave())
                .authDelete(domain.getAuthDelete())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }

    public static UserGroupMenuAuthResponse fromDomain(UserGroupMenuAuth domain, Menu menu) {
        if (domain == null) {
            return null;
        }
        UserGroupMenuAuthResponse response = fromDomain(domain);
        if (menu != null) {
            response.setMenuCode(menu.getMenuId());
            response.setMenuName(menu.getMenuName());
            response.setParentId(menu.getParentId());
            response.setMenuLevel(menu.getMenuLevel());
            response.setDisplayOrder(menu.getDisplayOrder());
            response.setFilePath(menu.getFilePath());
            response.setRouterPath(menu.getRouterPath());
        }
        return response;
    }
}
