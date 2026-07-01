package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDefSaveRequestDto {
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
