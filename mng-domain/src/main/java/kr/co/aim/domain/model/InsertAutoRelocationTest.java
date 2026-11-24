package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.InsertAutoRelocationTestCreateCommand;
import kr.co.aim.domain.command.InsertAutoRelocationTestUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertAutoRelocationTest implements HasTransactionInfo {
    private Long id;
    private String wareHouseName1;
    private String wareHouseName2;
    private String zoneName1;
    private String zoneName2;
    private String carrierName;
    private String state;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static InsertAutoRelocationTest create(InsertAutoRelocationTestCreateCommand command) {
        InsertAutoRelocationTest test = InsertAutoRelocationTest.builder()
                .id(TsidUtils.nextId())
                .wareHouseName1(command.getWareHouseName1())
                .wareHouseName2(command.getWareHouseName2())
                .zoneName1(command.getZoneName1())
                .zoneName2(command.getZoneName2())
                .carrierName(command.getCarrierName())
                .state(command.getState())
                .build();

        if (command.getTransactionInfo() != null) {
            test.setEventName(command.getTransactionInfo().eventName());
            test.setEventTime(command.getTransactionInfo().eventTime());
            test.setEventUser(command.getTransactionInfo().eventUser());
            test.setEventComment(command.getTransactionInfo().eventComment());
        }
        return test;
    }

    public InsertAutoRelocationTest update(InsertAutoRelocationTestUpdateCommand command) {
        if (command.getWareHouseName1() != null) {
            setWareHouseName1(command.getWareHouseName1());
        }
        if (command.getWareHouseName2() != null) {
            setWareHouseName2(command.getWareHouseName2());
        }
        if (command.getZoneName1() != null) {
            setZoneName1(command.getZoneName1());
        }
        if (command.getZoneName2() != null) {
            setZoneName2(command.getZoneName2());
        }
        if (command.getCarrierName() != null) {
            setCarrierName(command.getCarrierName());
        }
        if (command.getState() != null) {
            setState(command.getState());
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
