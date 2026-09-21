package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.SysUserCreateCommand;
import kr.co.aim.domain.command.SysUserUpdateCommand;
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
    private String departmentName;
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

    public static SysUser create(SysUserCreateCommand command) {
        return SysUser.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .userId(command.getUserId())
                .passwordHash(command.getPasswordHash())
                .userName(command.getUserName())
                .departmentId(command.getDepartmentId())
                .email(command.getEmail())
                .phoneNumber(command.getPhoneNumber())
                .userState(command.getUserState() != null ? command.getUserState() : "ACTIVE")
                .failedLoginCount(0)
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }

    public SysUser update(SysUserUpdateCommand command) {
        if (command.getFactoryName() != null) {
            setFactoryName(command.getFactoryName());
        }
        if (command.getPasswordHash() != null) {
            setPasswordHash(command.getPasswordHash());
            setPasswordChangeTime(command.getPasswordChangeTime());
        }
        if (command.getUserName() != null) {
            setUserName(command.getUserName());
        }
        if (command.getDepartmentId() != null) {
            setDepartmentId(command.getDepartmentId());
        }
        if (command.getEmail() != null) {
            setEmail(command.getEmail());
        }
        if (command.getPhoneNumber() != null) {
            setPhoneNumber(command.getPhoneNumber());
        }
        if (command.getUserState() != null) {
            setUserState(command.getUserState());
        }
        if (command.getFailedLoginCount() != null) {
            setFailedLoginCount(command.getFailedLoginCount());
        }
        if (command.getTransactionInfo() != null) {
            setEventName(command.getTransactionInfo().eventName());
            setEventTime(command.getTransactionInfo().eventTime());
            setEventUser(command.getTransactionInfo().eventUser());
            setEventComment(command.getTransactionInfo().eventComment());
        }
        return this;
    }
}

