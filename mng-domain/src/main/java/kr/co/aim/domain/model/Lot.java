package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.LotChangeCommand;
import kr.co.aim.domain.command.LotCreateCommand;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Lot implements HasTransactionInfo {
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

    public void change(LotChangeCommand command){
        this.apply(command.getTransactionInfo());
        setLotName(ObjectUtils.isEmpty(command.getLotName()) ? getLotName() : command.getLotName());
        setOriginalLotName(ObjectUtils.isEmpty(command.getOriginalLotName()) ? getOriginalLotName() :command.getOriginalLotName());
        setLotStatus(ObjectUtils.isEmpty(command.getLotStatus()) ? getLotStatus() :command.getLotStatus());
        setItemId(ObjectUtils.isEmpty(command.getItemId()) ? getItemId() :command.getItemId());
        setTotalQuantity(ObjectUtils.isEmpty(command.getTotalQuantity()) ? getTotalQuantity() :command.getTotalQuantity());
        setHoldState(ObjectUtils.isEmpty(command.getHoldState()) ? getHoldState() :command.getHoldState());
        setReasonCode(ObjectUtils.isEmpty(command.getReasonCode()) ? getReasonCode() :command.getReasonCode());
    }
}