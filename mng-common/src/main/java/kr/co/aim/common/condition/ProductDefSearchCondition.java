package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class ProductDefSearchCondition {
    private Long id;
    private String productDefName;
    private String factoryName;
    private String description1;
    private String description2;
    private BigDecimal ratio;
    private BigDecimal defaultReceiveQuantity;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

}