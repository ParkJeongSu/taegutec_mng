package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ZoneMappingCreateCommand;
import kr.co.aim.domain.command.ZoneMappingUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneMapping implements HasTransactionInfo {
    private Long id;
    private Long zoneId;
    private String orderLineNumber;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static ZoneMapping create(ZoneMappingCreateCommand command) {
        ZoneMapping zoneMapping = ZoneMapping.builder()
                .id(TsidUtils.nextId())
                .zoneId(command.getZoneId())
                .orderLineNumber(command.getOrderLineNumber())
                .build();

        if (command.getTransactionInfo() != null) {
            zoneMapping.setEventName(command.getTransactionInfo().eventName());
            zoneMapping.setEventTime(command.getTransactionInfo().eventTime());
            zoneMapping.setEventUser(command.getTransactionInfo().eventUser());
            zoneMapping.setEventComment(command.getTransactionInfo().eventComment());
        }
        return zoneMapping;
    }

    public ZoneMapping update(ZoneMappingUpdateCommand command) {
        if (command.getZoneId() != null) {
            setZoneId(command.getZoneId());
        }
        if (command.getOrderLineNumber() != null) {
            setOrderLineNumber(command.getOrderLineNumber());
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
