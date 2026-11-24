package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ZoneCreateCommand;
import kr.co.aim.domain.command.ZoneUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Zone implements HasTransactionInfo {
    private Long id;
    private String zoneName;
    private String zoneType;
    private String description;
    private String zoneColor;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Zone create(ZoneCreateCommand command) {
        Zone zone = Zone.builder()
                .id(TsidUtils.nextId())
                .zoneName(command.getZoneName())
                .zoneType(command.getZoneType())
                .description(command.getDescription())
                .zoneColor(command.getZoneColor())
                .build();

        if (command.getTransactionInfo() != null) {
            zone.setEventName(command.getTransactionInfo().eventName());
            zone.setEventTime(command.getTransactionInfo().eventTime());
            zone.setEventUser(command.getTransactionInfo().eventUser());
            zone.setEventComment(command.getTransactionInfo().eventComment());
        }
        return zone;
    }

    public Zone update(ZoneUpdateCommand command) {
        if (command.getZoneName() != null) {
            setZoneName(command.getZoneName());
        }
        if (command.getZoneType() != null) {
            setZoneType(command.getZoneType());
        }
        if (command.getDescription() != null) {
            setDescription(command.getDescription());
        }
        if (command.getZoneColor() != null) {
            setZoneColor(command.getZoneColor());
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
