package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthMenuSearchConditionDto {
    private Long authorityId;
    private Long systemDefId;
    private Long menuId;
}