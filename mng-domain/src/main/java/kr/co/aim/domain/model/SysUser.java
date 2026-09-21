package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private String userId;
    private String passwordHash;
    private String userName;
    private Long departmentId;
    private String email;
    private String phoneNumber;
    private String userState;
    private Integer failedLoginCount;
    private LocalDateTime lastLoginTime;
    private LocalDateTime passwordChangeTime;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
