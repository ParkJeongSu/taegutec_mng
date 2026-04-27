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
@Table(name = "CARRIER_DEF", catalog = "NEXBEEAS", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class CarrierDefEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "carrierDefName")
    private String carrierDefName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "carrierType")
    private String carrierType;

    @Column(name = "carrierDetailType")
    private String carrierDetailType;

    @Column(name = "defaultCapacity")
    private Integer defaultCapacity;

    @Column(name = "useCountLimit")
    private Integer useCountLimit;

    @Column(name = "useDurationLimit")
    private Integer useDurationLimit;

    @Column(name = "countLimitPerClean")
    private Integer countLimitPerClean;

    @Column(name = "durationLimitPerClean")
    private Integer durationLimitPerClean;

    @Column(name = "cleanCountLimit")
    private Integer cleanCountLimit;

    @Column(name = "checkoutState")
    private String checkOutState;

    @Column(name = "checkoutTime")
    private LocalDateTime checkOutTime;

    @Column(name = "checkoutUser")
    private String checkOutUser;

    @Column(name = "dataState")
    private String dataState;

    @Column(name = "lastEventName")
    private String eventName;

    @Column(name = "lastEventTime")
    private LocalDateTime eventTime;

    @Column(name = "lastEventUser")
    private String eventUser;

    @Column(name = "lastEventComment")
    private String eventComment;
}
