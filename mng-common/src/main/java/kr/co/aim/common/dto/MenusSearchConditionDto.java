package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class MenusSearchConditionDto {

    private Long id;
    private String menuName;
    private String viewURL;
    private String menuType;
    private Long systemDefId;
}