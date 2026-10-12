package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.InsertAutoTestCreateCommand;
import kr.co.aim.domain.command.InsertAutoTestUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertAutoTest implements HasTransactionInfo {
    private Long id;
    private String wareHouseName;
    private String workStationId;
    private String carrierName;
    private String zoneName;
    private String inboundPortName;
    private String travelProfile;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static InsertAutoTest create(InsertAutoTestCreateCommand command) {
        InsertAutoTest test = InsertAutoTest.builder()
                .id(TsidUtils.nextId())
                .wareHouseName(command.getWareHouseName())
                .workStationId(command.getWorkStationId())
                .carrierName(command.getCarrierName())
                .zoneName(command.getZoneName())
                .inboundPortName(command.getInboundPortName())
                .travelProfile(command.getTravelProfile())
                .build();

        if (command.getTransactionInfo() != null) {
            test.setEventName(command.getTransactionInfo().eventName());
            test.setEventTime(command.getTransactionInfo().eventTime());
            test.setEventUser(command.getTransactionInfo().eventUser());
            test.setEventComment(command.getTransactionInfo().eventComment());
        }
        return test;
    }

    public InsertAutoTest update(InsertAutoTestUpdateCommand command) {

        if (command.getWareHouseName() != null) {
            setWareHouseName(command.getWareHouseName());
        }
        if (command.getWorkStationId() != null) {
            setWorkStationId(command.getWorkStationId());
        }
        if (command.getCarrierName() != null) {
            setCarrierName(command.getCarrierName());
        }
        if (command.getZoneName() != null) {
            setZoneName(command.getZoneName());
        }
        if (command.getTravelProfile() != null) {
            setTravelProfile(command.getTravelProfile());
        }
        if (command.getInboundPortName() != null) {
            setInboundPortName(command.getInboundPortName());
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
