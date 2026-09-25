package kr.co.aim.api.dto;

import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsTransferCommandHistoryResponse {

    private String eventTimeKey;
    private String transferCommandName;
    private String carrierName;
    private String commandStatus;
    private String currentEquipmentName;
    private String orderType;
    private String source;
    private String target;
    private String targetEquipmentName;
    private Integer subCommandJobNo;
    private String subCommandStatus;
    private LocalDateTime jobStartTime;
    private LocalDateTime jobCompletedTime;
    private LocalDateTime createTime;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static WcsTransferCommandHistoryResponse fromEntity(WcsTransferCommandHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return WcsTransferCommandHistoryResponse.builder()
                .eventTimeKey(entity.getEventTimeKey())
                .transferCommandName(entity.getTransferCommandName())
                .carrierName(entity.getCarrierName())
                .commandStatus(entity.getCommandStatus())
                .currentEquipmentName(entity.getCurrentEquipmentName())
                .orderType(entity.getOrderType())
                .source(entity.getSource())
                .target(entity.getTarget())
                .targetEquipmentName(entity.getTargetEquipmentName())
                .subCommandJobNo(entity.getSubCommandJobNo())
                .subCommandStatus(entity.getSubCommandStatus())
                .jobStartTime(entity.getJobStartTime())
                .jobCompletedTime(entity.getJobCompletedTime())
                .createTime(entity.getCreateTime())
                .eventName(entity.getEventName())
                .eventTime(entity.getEventTime())
                .eventUser(entity.getEventUser())
                .eventComment(entity.getEventComment())
                .build();
    }
}
