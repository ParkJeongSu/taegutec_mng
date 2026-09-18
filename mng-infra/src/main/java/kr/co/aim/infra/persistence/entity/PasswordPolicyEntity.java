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
@Table(name = "PASSWORD_POLICY", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordPolicyEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "POLICY_NAME")
    private String policyName;

    @Column(name = "MIN_LENGTH")
    private Integer minLength;

    @Column(name = "MAX_LENGTH")
    private Integer maxLength;

    @Column(name = "REQUIRE_SPECIAL_CHAR")
    private String requireSpecialChar;

    @Column(name = "EXPIRATION_DAYS")
    private Integer expirationDays;

    @Column(name = "MAX_FAILED_ATTEMPTS")
    private Integer maxFailedAttempts;

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
