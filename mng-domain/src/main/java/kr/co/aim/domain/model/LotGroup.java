package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.LotGroupChangeCommand;
import kr.co.aim.domain.command.LotGroupCreateCommand;
import kr.co.aim.domain.command.LotGroupSplitCommand;
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
public class LotGroup implements HasTransactionInfo {
    private Long id;
    private String lotGroupName;
    private String originalLotGroupName;
    private String lotGroupStatus;
    private String itemId;
    private BigDecimal totalQuantity;
    private String holdState;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static LotGroup create(LotGroupCreateCommand command) {
        return LotGroup.builder()
                .id(TsidUtils.nextId())
                .lotGroupName(command.getLotGroupName())
                .originalLotGroupName(command.getOriginalLotGroupName())
                .lotGroupStatus(command.getLotGroupStatus())
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

    public void change(LotGroupChangeCommand command){
        this.apply(command.getTransactionInfo());
        setLotGroupName(ObjectUtils.isEmpty(command.getLotGroupName()) ? getLotGroupName() : command.getLotGroupName());
        setOriginalLotGroupName(ObjectUtils.isEmpty(command.getOriginalLotGroupName()) ? getOriginalLotGroupName() :command.getOriginalLotGroupName());
        setLotGroupStatus(ObjectUtils.isEmpty(command.getLotGroupStatus()) ? getLotGroupStatus() :command.getLotGroupStatus());
        setItemId(ObjectUtils.isEmpty(command.getItemId()) ? getItemId() :command.getItemId());
        setTotalQuantity(ObjectUtils.isEmpty(command.getTotalQuantity()) ? getTotalQuantity() :command.getTotalQuantity());
        setHoldState(ObjectUtils.isEmpty(command.getHoldState()) ? getHoldState() :command.getHoldState());
        setReasonCode(ObjectUtils.isEmpty(command.getReasonCode()) ? getReasonCode() :command.getReasonCode());
    }

    public LotGroup split(LotGroupSplitCommand command){
        int compareResult = getTotalQuantity().compareTo(command.getSplitQuantity());

        if(compareResult > 0){
            // totalQuantity > SplitQuantity
            setTotalQuantity(getTotalQuantity().subtract(command.getSplitQuantity()));
            return LotGroup.builder()
                    .id(TsidUtils.nextId())
                    .lotGroupName(command.getLotGroupName())
                    .originalLotGroupName(command.getOriginalLotGroupName())
                    .lotGroupStatus(command.getLotGroupStatus())
                    .itemId(command.getItemId())
                    .totalQuantity(command.getSplitQuantity())
                    .holdState(command.getHoldState())
                    .reasonCode(command.getReasonCode())
                    .eventName(command.getTransactionInfo().eventName())
                    .eventTime(command.getTransactionInfo().eventTime())
                    .eventUser(command.getTransactionInfo().eventUser())
                    .eventComment(command.getTransactionInfo().eventComment())
                    .build();
        }
        else if(compareResult < 0){
            // split 하려는 수량이 현재 talal 수량보다 크다면 error
            throw new RuntimeException("splitQuantity > totalQuantity");
        }
        else {
            // split 하려는 수량과 같으면 기존 Lot은 수량을 0으로 변경
            // 신규 LotGroup 생성
            this.apply(command.getTransactionInfo());
            setLotGroupName(ObjectUtils.isEmpty(command.getLotGroupName()) ? getLotGroupName() : command.getLotGroupName());
            setOriginalLotGroupName(ObjectUtils.isEmpty(command.getOriginalLotGroupName()) ? getOriginalLotGroupName() :command.getOriginalLotGroupName());
            setLotGroupStatus(ObjectUtils.isEmpty(command.getLotGroupStatus()) ? getLotGroupStatus() :command.getLotGroupStatus());
            setItemId(ObjectUtils.isEmpty(command.getItemId()) ? getItemId() :command.getItemId());
            setHoldState(ObjectUtils.isEmpty(command.getHoldState()) ? getHoldState() :command.getHoldState());
            setReasonCode(ObjectUtils.isEmpty(command.getReasonCode()) ? getReasonCode() :command.getReasonCode());
            setTotalQuantity(new BigDecimal(0));
            return LotGroup.builder()
                    .id(TsidUtils.nextId())
                    .lotGroupName(command.getLotGroupName())
                    .originalLotGroupName(command.getOriginalLotGroupName())
                    .lotGroupStatus(command.getLotGroupStatus())
                    .itemId(command.getItemId())
                    .totalQuantity(command.getSplitQuantity())
                    .holdState(command.getHoldState())
                    .reasonCode(command.getReasonCode())
                    .eventName(command.getTransactionInfo().eventName())
                    .eventTime(command.getTransactionInfo().eventTime())
                    .eventUser(command.getTransactionInfo().eventUser())
                    .eventComment(command.getTransactionInfo().eventComment())
                    .build();
        }
    }
}