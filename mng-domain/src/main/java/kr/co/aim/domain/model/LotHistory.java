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
public class LotHistory implements IBaseHistoryEntity {
    private Long id;
    private String lotName;
    private String originalLotName;
    private String lotStatus;
    private String itemId;
    private BigDecimal totalQuantity;
    private String holdState;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}