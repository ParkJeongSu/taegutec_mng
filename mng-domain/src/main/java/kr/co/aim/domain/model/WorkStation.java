package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.WorkStationCreateCommand;
import kr.co.aim.domain.command.WorkStationUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkStation implements HasTransactionInfo {
    private Long id;
    private String workStationName;
    private String equipmentName;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static WorkStation create(WorkStationCreateCommand command) {
        WorkStation workStation = WorkStation.builder()
                .id(TsidUtils.nextId())
                .workStationName(command.getWorkStationName())
                .equipmentName(command.getEquipmentName())
                .build();

        if (command.getTransactionInfo() != null) {
            workStation.setEventName(command.getTransactionInfo().eventName());
            workStation.setEventTime(command.getTransactionInfo().eventTime());
            workStation.setEventUser(command.getTransactionInfo().eventUser());
            workStation.setEventComment(command.getTransactionInfo().eventComment());
        }
        return workStation;
    }

    public WorkStation update(WorkStationUpdateCommand command) {
        if (command.getWorkStationName() != null) {
            setWorkStationName(command.getWorkStationName());
        }
        if (command.getEquipmentName() != null) {
            setEquipmentName(command.getEquipmentName());
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
