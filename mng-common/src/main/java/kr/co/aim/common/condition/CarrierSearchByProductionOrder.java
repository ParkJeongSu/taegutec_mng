package kr.co.aim.common.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class CarrierSearchByProductionOrder {
    private String orderId;
    private String orderLineNumber;
}