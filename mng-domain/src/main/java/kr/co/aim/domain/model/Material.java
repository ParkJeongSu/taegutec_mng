package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MaterialCreateCommand;
import kr.co.aim.domain.command.MaterialUpdateCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Material implements HasTransactionInfo {
    private Long id;
    private String materialId;
    private String description;
    private Long materialClassId;
    private Long materialGroupId;
    private String carrierName;
    private BigDecimal quantity;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Material create(MaterialCreateCommand command) {
        Material material = Material.builder()
                .id(TsidUtils.nextId())
                .materialId(command.getMaterialId())
                .description(command.getDescription())
                .materialClassId(command.getMaterialClassId())
                .materialGroupId(command.getMaterialGroupId())
                .carrierName(command.getCarrierName())
                .quantity(command.getQuantity())
                .build();

        if (command.getTransactionInfo() != null) {
            material.setEventName(command.getTransactionInfo().eventName());
            material.setEventTime(command.getTransactionInfo().eventTime());
            material.setEventUser(command.getTransactionInfo().eventUser());
            material.setEventComment(command.getTransactionInfo().eventComment());
        }
        return material;
    }

    public Material update(MaterialUpdateCommand command) {
        if (command.getMaterialId() != null) {
            setMaterialId(command.getMaterialId());
        }
        if (command.getDescription() != null) {
            setDescription(command.getDescription());
        }
        if (command.getMaterialClassId() != null) {
            setMaterialClassId(command.getMaterialClassId());
        }
        if (command.getMaterialGroupId() != null) {
            setMaterialGroupId(command.getMaterialGroupId());
        }
        if (command.getCarrierName() != null) {
            setCarrierName(command.getCarrierName());
        }
        if (command.getQuantity() != null) {
            setQuantity(command.getQuantity());
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
