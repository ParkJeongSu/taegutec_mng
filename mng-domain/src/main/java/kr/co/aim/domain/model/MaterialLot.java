package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MaterialLotCreateCommand;
import kr.co.aim.domain.command.MaterialLotUpdateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialLot implements HasTransactionInfo {
    private Long id;
    private String materialLotId;
    private Long materialDefId;
    private Long parentMaterialLotId;
    private String carrierName;
    private BigDecimal quantity;
    private String state;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static MaterialLot create(MaterialLotCreateCommand command) {
        MaterialLot materialLot = MaterialLot.builder()
                .id(TsidUtils.nextId())
                .materialLotId(command.getMaterialLotId())
                .materialDefId(command.getMaterialDefId())
                .parentMaterialLotId(command.getParentMaterialLotId())
                .carrierName(command.getCarrierName())
                .quantity(command.getQuantity())
                .state(command.getState())
                .build();

        if (command.getTransactionInfo() != null) {
            materialLot.setEventName(command.getTransactionInfo().eventName());
            materialLot.setEventTime(command.getTransactionInfo().eventTime());
            materialLot.setEventUser(command.getTransactionInfo().eventUser());
            materialLot.setEventComment(command.getTransactionInfo().eventComment());
        }
        return materialLot;
    }

    public MaterialLot update(MaterialLotUpdateCommand command) {
        if (command.getMaterialLotId() != null) {
            setMaterialLotId(command.getMaterialLotId());
        }
        if (command.getMaterialDefId() != null) {
            setMaterialDefId(command.getMaterialDefId());
        }
        if (command.getParentMaterialLotId() != null) {
            setParentMaterialLotId(command.getParentMaterialLotId());
        }
        if (command.getCarrierName() != null) {
            setCarrierName(command.getCarrierName());
        }
        if (command.getQuantity() != null) {
            setQuantity(command.getQuantity());
        }
        if (command.getState() != null) {
            setState(command.getState());
        }
        if (command.getTransactionInfo() != null) {
            setEventName(command.getTransactionInfo().eventName());
            setEventTime(command.getTransactionInfo().eventTime());
            setEventUser(command.getTransactionInfo().eventUser());
            setEventComment(command.getTransactionInfo().eventComment());
        }
        return this;
    }
}
