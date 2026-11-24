package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.FactoryCreateCommand;
import kr.co.aim.domain.command.FactoryUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Factory implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private String description;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Factory create(FactoryCreateCommand command) {
        Factory factory = Factory.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .description(command.getDescription())
                .build();

        if (command.getTransactionInfo() != null) {
            factory.setEventName(command.getTransactionInfo().eventName());
            factory.setEventTime(command.getTransactionInfo().eventTime());
            factory.setEventUser(command.getTransactionInfo().eventUser());
            factory.setEventComment(command.getTransactionInfo().eventComment());
        }
        return factory;
    }

    public Factory update(FactoryUpdateCommand command) {
        if (command.getFactoryName() != null) {
            setFactoryName(command.getFactoryName());
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
