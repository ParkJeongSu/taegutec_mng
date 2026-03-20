package kr.co.aim.domain.model;
import jakarta.persistence.Id;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.TransportJobCreateCommand;
import kr.co.aim.domain.command.TransportJobUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransportJob implements HasTransactionInfo {
    private Long id;
    private String transportJobName;
    private String carrierName;
    private String transportJobState;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourceShelfName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationShelfName;
    private Integer priority;
    private String errorCode;
    private String errorText;
    private String requestType;
    private LocalDateTime createTime;
    private LocalDateTime departedTime;
    private LocalDateTime arrivedTime;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private Long orderId;

    public static TransportJob create(TransportJobCreateCommand command){
        return TransportJob.builder()
                .transportJobName(command.getTransportJobName())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeTransportJob(TransportJobUpdateCommand command){
        this.apply(command.getTransactionInfo());
    }
}
