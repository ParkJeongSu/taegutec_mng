package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchConditionDto {
    private String userId;
    private Long authorityId;
    private String userName;
    private String password;
    private String email;
    private String phone1;
}