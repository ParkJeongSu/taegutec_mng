package kr.co.aim.api.dto;

import kr.co.aim.domain.model.Menu;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorizedMenuResponse {

    private Long id;                  // MENU.ID (TSID)
    private String factoryName;       // 소속 공장
    private String menuId;            // MENU.MENU_ID (예: 'TRANSFER_CMD_HISTORY')
    private String menuName;          // 메뉴 한글명
    private Long parentId;            // 상위 메뉴 ID
    private Integer menuLevel;        // 메뉴 레벨 (1, 2)
    private String routerPath;        // 라우터 경로
    private String filePath;          // 컴포넌트 파일 경로
    private String iconName;          // 아이콘명
    private Integer displayOrder;     // 정렬 순서
    private String isVisible;         // 'Y' / 'N'

    @Builder.Default
    private List<UserAuthorizedMenuResponse> children = new ArrayList<>();

    public static UserAuthorizedMenuResponse fromDomain(Menu domain) {
        if (domain == null) {
            return null;
        }

        return UserAuthorizedMenuResponse.builder()
                .id(domain.getId())
                .factoryName(domain.getFactoryName())
                .menuId(domain.getMenuId())
                .menuName(domain.getMenuName())
                .parentId(domain.getParentId())
                .menuLevel(domain.getMenuLevel())
                .routerPath(domain.getRouterPath())
                .filePath(domain.getFilePath())
                .iconName(domain.getIconName())
                .displayOrder(domain.getDisplayOrder())
                .isVisible(domain.getIsVisible())
                .children(new ArrayList<>())
                .build();
    }
}
