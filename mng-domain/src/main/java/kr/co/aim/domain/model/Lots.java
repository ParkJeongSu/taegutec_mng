package kr.co.aim.domain.model;

import kr.co.aim.common.enums.LotProcessState;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.LotsCreateCommand;
import kr.co.aim.domain.command.LotsUpdateCommand;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lots implements HasTransactionInfo {
    private Long id;
    private String lotName;
    private String productionType;
    private String lotState;
    private String processState;
    // TODO: 현재 MNG 에서 spec, flow 등을 관리 안함 그래서 아래의 컬럼을 단순히 Name으로 변경
    private String productDefId;
    private String processSpecId;
    private String processSpecVersion;
    private String processFlowId;
    private String processOperationId;
    private String workOrderId; // TODO: Long type 으로 변경
    private String equipmentName;
    private String portName;
    private String recipeName;
    private Long carrierId;
    private Integer priority;
    private String lotGrade;
    private String productionDetailType;
    private LocalDateTime planStartDate;
    private LocalDateTime planDueDate;
    private LocalDateTime createTime;
    private LocalDateTime releaseTime;
    private LocalDateTime shipTime;
    private LocalDateTime trackInTime;
    private LocalDateTime trackOutTime;
    private LocalDateTime operationMoveTime;
    private Integer quantity;
    private Integer oldQuantity;
    private String holdState;
    private String reworkState;
    private Integer reworkCount;
    private String originalProcessSpecId;
    private String originalProcessSpecVersion;
    private String returnProcessFlowId;
    private String returnProcessOperationId;
    private String reasonCode;
    private String ownerCode;
    private String eventName;
    
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Lots create(LotsCreateCommand command){
        return Lots.builder()
                .lotName(command.getLotName())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment()).
                build();
    }
    public void changeLots(LotsUpdateCommand command) {
        this.apply(command.getTransactionInfo());
    }

    public void processJobStarted(ProcessJobStartedCommand command){
        this.apply(command.getTransactionInfo());
        setEquipmentName(command.getEquipmentName());
        setRecipeName(command.getRecipeName());
        setProcessState(LotProcessState.RUN.getValue());
        setTrackInTime(command.getTransactionInfo().eventTime());
    }
    public void processJobEnded(ProcessJobEndedCommand command){
        this.apply(command.getTransactionInfo());
        setEquipmentName(command.getEquipmentName());
        setRecipeName(command.getRecipeName());
        setProcessState(LotProcessState.WAIT.getValue());
        setTrackOutTime(command.getTransactionInfo().eventTime());
    }

}
