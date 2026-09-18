package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements HasTransactionInfo {

    private Long id;
    private String factoryName;
    private String userId;
    private String userName;
    private String password;
    private String departmentName;
    private String email;
    private String phoneNumber;
    private String userState;
    private LocalDateTime passwordChangeTime;
    private Integer failedLoginAttempts;
    private LocalDateTime lastLoginTime;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
