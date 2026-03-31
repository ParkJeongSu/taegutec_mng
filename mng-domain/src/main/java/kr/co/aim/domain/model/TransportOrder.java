package kr.co.aim.domain.model;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.command.TransportOrderCreateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransportOrder {
    private Long id;
    private String transportOrderId;
    private Long idocId;
    private String description;
    private String carrierName;
    private String transportType;
    private String transportStatus;
    private String lastTransactionCode;
    private String carrierType;
    private Integer priority;
    private Long galId; // cGalId only reply need
    private String galWarehouse; // cGalWarehouse only reply need
    private String locationId; // storage location Number 저장 위치 xxyyzz…
    private String workStationId; //Inbound : 현재위치, Outbound : 타겟의 위치 Outbound 에서 목적지로 사용
    private String sourceZoneName;
    private String destinationZoneName;
    private String errorText;
    private String actualWeight;
    private String requestedZoneName;
    private String actualZoneName;
    private String actualLocationId;
    private String drivingProfile;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime completeTime;
    private LocalDateTime retrievalTime;
    private String createUser;
    private String releaseUser;
    private String completeUser;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static TransportOrder create (TransportOrderCreateCommand command){
        return
                TransportOrder
                        .builder()
                        .id(TsidUtils.nextId())
                        .transportOrderId(command.getTransportOrderId())
                        .idocId(command.getIdocId())
                        .description(command.getDescription())
                        .carrierName(command.getCarrierName())
                        .transportType(command.getTransportType())
                        .transportStatus(command.getTransportStatus())
                        .lastTransactionCode(command.getLastTransactionCode())
                        .carrierType(command.getCarrierType())
                        .priority(command.getPriority())
                        .galId(command.getGalId())
                        .galWarehouse(command.getGalWarehouse())
                        .locationId(command.getLocationId())
                        .workStationId(command.getWorkStationId())
                        .sourceZoneName(command.getSourceZoneName())
                        .destinationZoneName(command.getDestinationZoneName())
                        .errorText(command.getErrorText())
                        .actualWeight(command.getActualWeight())
                        .requestedZoneName(command.getRequestedZoneName())
                        .actualZoneName(command.getActualZoneName())
                        .actualLocationId(command.getActualLocationId())
                        .drivingProfile(command.getDrivingProfile())
                        .createTime(command.getCreateTime())
                        .releaseTime(command.getReleaseTime())
                        .completeTime(command.getCompleteTime())
                        .retrievalTime(command.getRetrievalTime())
                        .createUser(command.getCreateUser())
                        .releaseUser(command.getReleaseUser())
                        .completeUser(command.getCompleteUser())
                        .eventName(command.getTransactionInfo().eventName())
                        .eventTime(command.getTransactionInfo().eventTime())
                        .eventUser(command.getTransactionInfo().eventUser())
                        .eventComment(command.getTransactionInfo().eventComment())
                        .build();
    }

}
