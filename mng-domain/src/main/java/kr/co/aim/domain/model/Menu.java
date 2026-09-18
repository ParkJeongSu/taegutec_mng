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
    private String menuName;
    private Long parentMenuId;
    private Integer menuLevel;
    private Integer displayOrder;
    private String viewUrl;
    private String iconName;
    private String description;
    private String menuType;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
