package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class MenusCreateRequestDto {

    private Long id;
    private Long systemDefId;
    private String menuName;
    private Long parentMenuId;
    private String viewURL;
    private Integer menuSEQ;
    private String description;
    private String iconName;
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