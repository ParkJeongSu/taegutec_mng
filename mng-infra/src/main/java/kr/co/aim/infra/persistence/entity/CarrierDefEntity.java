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
@Table(name = "CARRIER_DEF")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class CarrierDefEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CARRIER_DEF_NAME")
    private String carrierDefName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CARRIER_TYPE")
    private String carrierType;

    @Column(name = "CARRIER_DETAIL_TYPE")
    private String carrierDetailType;

    @Column(name = "DEFAULT_CAPACITY")
    private Integer defaultCapacity;

    @Column(name = "USE_COUNT_LIMIT")
    private Integer useCountLimit;

    @Column(name = "USE_DURATION_LIMIT")
    private Integer useDurationLimit;

    @Column(name = "COUNT_LIMIT_PER_CLEAN")
    private Integer countLimitPerClean;

    @Column(name = "DURATION_LIMIT_PER_CLEAN")
    private Integer durationLimitPerClean;

    @Column(name = "CLEAN_COUNT_LIMIT")
    private Integer cleanCountLimit;

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
