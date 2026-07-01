package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.command.LotCreateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Lot {
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

    public static Lot create(LotCreateCommand command) {
        return Lot.builder()
                .id(TsidUtils.nextId())
                .lotName(command.getLotName())
                .originalLotName(command.getOriginalLotName())
                .lotStatus(command.getLotStatus())
                .itemId(command.getItemId())
                .totalQuantity(command.getTotalQuantity())
                .holdState(command.getHoldState())
                .reasonCode(command.getReasonCode())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();
    }
}