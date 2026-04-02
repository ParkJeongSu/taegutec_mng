package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.command.IfEventQueueCreateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class IfEventQueue {
    private Long id;
    private String eventType;
    private String payload;
    private String ifStatus;
    private String carrierName;
    private String idocId;
    private String orderId;
    private String orderLineNumber;
    private Integer retryCNT;
    private String errMSG;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static IfEventQueue create(IfEventQueueCreateCommand command) {
        return IfEventQueue
                .builder()
                .id(TsidUtils.nextId())
                .eventType(command.getEventType())
                .payload(command.getPayload())
                .ifStatus(command.getIfStatus())
                .carrierName(command.getCarrierName())
                .idocId(command.getIdocId())
                .orderId(command.getOrderId())
                .orderLineNumber(command.getOrderLineNumber())
                .retryCNT(command.getRetryCNT())
                .errMSG(command.getErrMSG())
                .createTime(command.getTransactionInfo().eventTime())
                .build();
    }

}
