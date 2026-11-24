package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MaterialClassDefCreateCommand;
import kr.co.aim.domain.command.MaterialClassDefUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialClassDef implements HasTransactionInfo {
    private Long id;
    private String materialClassDefName;
    private String materialClassType;
    private Long parentClassId;
    private Integer classLevel;
    private String description;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static MaterialClassDef create(MaterialClassDefCreateCommand command) {
        MaterialClassDef materialClassDef = MaterialClassDef.builder()
                .id(TsidUtils.nextId())
                .materialClassDefName(command.getMaterialClassDefName())
                .materialClassType(command.getMaterialClassType())
                .parentClassId(command.getParentClassId())
                .classLevel(command.getClassLevel())
                .description(command.getDescription())
                .build();

        if (command.getTransactionInfo() != null) {
            materialClassDef.setEventName(command.getTransactionInfo().eventName());
            materialClassDef.setEventTime(command.getTransactionInfo().eventTime());
            materialClassDef.setEventUser(command.getTransactionInfo().eventUser());
            materialClassDef.setEventComment(command.getTransactionInfo().eventComment());
        }
        return materialClassDef;
    }

    public MaterialClassDef update(MaterialClassDefUpdateCommand command) {
        if (command.getMaterialClassDefName() != null) {
            setMaterialClassDefName(command.getMaterialClassDefName());
        }
        if (command.getMaterialClassType() != null) {
            setMaterialClassType(command.getMaterialClassType());
        }
        if (command.getParentClassId() != null) {
            setParentClassId(command.getParentClassId());
        }
        if (command.getClassLevel() != null) {
            setClassLevel(command.getClassLevel());
        }
        if (command.getDescription() != null) {
            setDescription(command.getDescription());
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
