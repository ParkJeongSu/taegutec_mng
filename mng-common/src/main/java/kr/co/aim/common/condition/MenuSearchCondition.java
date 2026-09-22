package kr.co.aim.common.condition;
 
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
@Schema(description = "메뉴 검색 조건")
public class MenuSearchCondition {
 
    @Schema(description = "공장 구분", example = "INSERT")
    private String factoryName;
 
    @Schema(description = "다국어 식별자", example = "MENU_USER_MNG")
    private String menuId;
 
    @Schema(description = "메뉴명 (기본 표시 이름)", example = "사용자 관리")
    private String menuName;
 
    @Schema(description = "부모 메뉴 ID", example = "877810665130787000")
    private Long parentId;
 
    @Schema(description = "메뉴 계층 레벨", example = "1")
    private Integer menuLevel;
 
    @Schema(description = "메뉴 표시 여부 (Y/N)", example = "Y")
    private String isVisible;
 
    @Schema(description = "사용 여부 (상태)", example = "ACTIVE")
    private String useState;
}
