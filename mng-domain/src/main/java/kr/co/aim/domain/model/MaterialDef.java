package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MaterialDefCreateCommand;
import kr.co.aim.domain.command.MaterialDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDef implements HasTransactionInfo {
    private Long id;
    private String materialDefName;
    private String description;
    private Long materialClassId;
    private Long materialGroupId;
    private String unit;
    private String isSerialTracked;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static MaterialDef create(MaterialDefCreateCommand command) {
        MaterialDef materialDef = MaterialDef.builder()
                .id(TsidUtils.nextId())
                .materialDefName(command.getMaterialDefName())
                .description(command.getDescription())
                .materialClassId(command.getMaterialClassId())
                .materialGroupId(command.getMaterialGroupId())
                .unit(command.getUnit())
                .isSerialTracked(command.getIsSerialTracked())
                .build();

        if (command.getTransactionInfo() != null) {
            materialDef.setEventName(command.getTransactionInfo().eventName());
            materialDef.setEventTime(command.getTransactionInfo().eventTime());
            materialDef.setEventUser(command.getTransactionInfo().eventUser());
            materialDef.setEventComment(command.getTransactionInfo().eventComment());
        }
        return materialDef;
    }

    public MaterialDef update(MaterialDefUpdateCommand command) {
        if (command.getMaterialDefName() != null) {
            setMaterialDefName(command.getMaterialDefName());
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
        if (command.getUnit() != null) {
            setUnit(command.getUnit());
        }
        if (command.getIsSerialTracked() != null) {
            setIsSerialTracked(command.getIsSerialTracked());
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
