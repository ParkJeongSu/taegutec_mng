package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "\"USER\"", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DEPARTMENT_NAME")
    private String departmentName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "USER_STATE")
    private String userState;

    @Column(name = "PASSWORD_CHANGE_TIME")
    private LocalDateTime passwordChangeTime;

    @Column(name = "FAILED_LOGIN_ATTEMPTS")
    private Integer failedLoginAttempts;

    @Column(name = "LAST_LOGIN_TIME")
    private LocalDateTime lastLoginTime;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name = "CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name = "CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name = "DATA_STATE")
    private String dataState;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
