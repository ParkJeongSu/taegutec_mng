package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MaterialGroupCreateCommand;
import kr.co.aim.domain.command.MaterialGroupUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialGroup implements HasTransactionInfo {
    private Long id;
    private String materialGroupName;
    private String description;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static MaterialGroup create(MaterialGroupCreateCommand command) {
        MaterialGroup materialGroup = MaterialGroup.builder()
                .id(TsidUtils.nextId())
                .materialGroupName(command.getMaterialGroupName())
                .description(command.getDescription())
                .build();

        if (command.getTransactionInfo() != null) {
            materialGroup.setEventName(command.getTransactionInfo().eventName());
            materialGroup.setEventTime(command.getTransactionInfo().eventTime());
            materialGroup.setEventUser(command.getTransactionInfo().eventUser());
            materialGroup.setEventComment(command.getTransactionInfo().eventComment());
        }
        return materialGroup;
    }

    public MaterialGroup update(MaterialGroupUpdateCommand command) {
        if (command.getMaterialGroupName() != null) {
            setMaterialGroupName(command.getMaterialGroupName());
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
