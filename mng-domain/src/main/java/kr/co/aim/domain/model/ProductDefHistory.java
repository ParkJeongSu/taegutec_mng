package kr.co.aim.domain.model;

import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDefHistory implements IBaseHistoryEntity {
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