package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.ZoneFallbackCreateCommand;
import kr.co.aim.domain.command.ZoneFallbackUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneFallback implements HasTransactionInfo {
    private Long id;
    private Long sourceZoneId;
    private Long targetZoneId;
    private Integer priority;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static ZoneFallback create(ZoneFallbackCreateCommand command) {
        ZoneFallback zoneFallback = ZoneFallback.builder()
                .id(TsidUtils.nextId())
                .sourceZoneId(command.getSourceZoneId())
                .targetZoneId(command.getTargetZoneId())
                .priority(command.getPriority())
                .build();

        if (command.getTransactionInfo() != null) {
            zoneFallback.setEventName(command.getTransactionInfo().eventName());
            zoneFallback.setEventTime(command.getTransactionInfo().eventTime());
            zoneFallback.setEventUser(command.getTransactionInfo().eventUser());
            zoneFallback.setEventComment(command.getTransactionInfo().eventComment());
        }
        return zoneFallback;
    }

    public ZoneFallback update(ZoneFallbackUpdateCommand command) {
        if (command.getSourceZoneId() != null) {
            setSourceZoneId(command.getSourceZoneId());
        }
        if (command.getTargetZoneId() != null) {
            setTargetZoneId(command.getTargetZoneId());
        }
        if (command.getPriority() != null) {
            setPriority(command.getPriority());
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
