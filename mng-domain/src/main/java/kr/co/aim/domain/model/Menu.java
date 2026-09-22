package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu implements HasTransactionInfo {

    private Long id;
    private String factoryName;
    private String menuId; // 다국어 식별자
    private String menuName; // 기본 표시 이름
    private Long parentId; // 부모 메뉴 id
    private Integer menuLevel;
    private String filePath; // 실제 vue 파일 위치
    private String routerPath; // 브라우저의 URL 경로
    private String iconName;
    private Integer displayOrder;
    private String isVisible;
    private String useState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
