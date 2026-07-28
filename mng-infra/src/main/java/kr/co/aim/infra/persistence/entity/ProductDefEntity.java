package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "PRODUCT_DEF", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDefEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRODUCT_DEF_NAME")
    private String productDefName;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "DESCRIPTION1")
    private String description1;

    @Column(name = "DESCRIPTION2")
    private String description2;

    @Column(name = "RATIO")
    private BigDecimal ratio;

    @Column(name = "DEFAULT_RECEIVE_QUANTITY")
    private BigDecimal defaultReceiveQuantity;

    @Column(name = "TOLERANCE_VAL")
    private BigDecimal toleranceVal;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}