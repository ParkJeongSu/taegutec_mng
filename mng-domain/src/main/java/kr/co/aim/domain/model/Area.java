package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.AreaCreateCommand;
import kr.co.aim.domain.command.AreaUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Area implements HasTransactionInfo {
    private Long id;
    private String areaName;
    private String description;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Area create(AreaCreateCommand command) {
        Area area = Area.builder()
                .id(TsidUtils.nextId())
                .areaName(command.getAreaName())
                .description(command.getDescription())
                .build();

        if (command.getTransactionInfo() != null) {
            area.setEventName(command.getTransactionInfo().eventName());
            area.setEventTime(command.getTransactionInfo().eventTime());
            area.setEventUser(command.getTransactionInfo().eventUser());
            area.setEventComment(command.getTransactionInfo().eventComment());
        }
        return area;
    }

    public Area update(AreaUpdateCommand command) {
        if (command.getAreaName() != null) {
            setAreaName(command.getAreaName());
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
