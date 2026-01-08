package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "PRODUCT_DEF_HISTORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class ProductDefHistoryEntity implements IBaseHistoryEntity {
    @Id
    @Column(name="ID")
    private Long id;

    @Column(name="PRODUCT_DEF_NAME")
    private String productDefName;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="PRODUCTION_TYPE")
    private String productionType;

    @Column(name="PRODUCTION_DETAIL_TYPE")
    private String productionDetailType;

    @Column(name="PRODUCT_TYPE")
    private String productType;

    @Column(name="PRODUCT_SUB_TYPE")
    private String productSubType;

    @Column(name="QUANTITY")
    private Integer quantity;

    @Column(name="SUB_QUANTITY")
    private Integer subQuantity;

    @Column(name="ESTIMATED_CYCLE_TIME")
    private Integer estimatedCycleTime;

    @Column(name="XCOUNT")
    private Integer xCount;

    @Column(name="YCOUNT")
    private Integer yCount;

    @Column(name="SECTION_NAME")
    private String sectionName;

    @Column(name="TECH_NAME")
    private String techName;

    @Column(name="DENSITY")
    private String density;

    @Column(name="GENERATION")
    private String generation;

    @Column(name="ORGANIZATION")
    private String organization;

    @Column(name="CHECK_OUT_STATE")
    private String checkOutState;

    @Column(name="CHECK_OUT_TIME")
    private LocalDateTime checkOutTime;

    @Column(name="CHECK_OUT_USER")
    private String checkOutUser;

    @Column(name="DATE_STATE")
    private String dataState;

    @Column(name="EVENT_NAME")
    private String eventName;

    @Column(name="EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name="EVENT_USER")
    private String eventUser;

    @Column(name="EVENT_COMMENT")
    private String eventComment;
}
