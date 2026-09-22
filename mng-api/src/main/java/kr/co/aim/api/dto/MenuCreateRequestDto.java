package kr.co.aim.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 등록 요청 DTO")
public class MenuCreateRequestDto {

    @Schema(description = "공장 구분", example = "INSERT", requiredMode = Schema.RequiredMode.REQUIRED)
    private String factoryName;

    @Schema(description = "다국어 식별자", example = "MENU_USER_MNG", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuId;

    @Schema(description = "메뉴 기본 표시 이름", example = "사용자 관리", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @Schema(description = "부모 메뉴 ID (TSID)", example = "877810665130787000")
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

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "신규 메뉴 생성")
    private String eventComment;
}
