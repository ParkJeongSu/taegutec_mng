package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserSearchCondition {
    private String factoryName;
    private String userId;
    private String userName;
    private Long departmentId;
    private String email;
    private String phoneNumber;
    private String userState;
}
