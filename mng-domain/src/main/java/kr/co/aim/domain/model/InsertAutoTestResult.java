package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.InsertAutoTestResultCreateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertAutoTestResult implements HasTransactionInfo {
    private Long id;
    private Long autoTestId;
    private String state;
    private String result;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static InsertAutoTestResult create(InsertAutoTestResultCreateCommand command) {
        InsertAutoTestResult testResult = InsertAutoTestResult.builder()
                .id(TsidUtils.nextId())
                .autoTestId(command.getAutoTestId())
                .state(command.getState())
                .result(command.getResult())
                .build();

        if (command.getTransactionInfo() != null) {
            testResult.setEventName(command.getTransactionInfo().eventName());
            testResult.setEventTime(command.getTransactionInfo().eventTime());
            testResult.setEventUser(command.getTransactionInfo().eventUser());
            testResult.setEventComment(command.getTransactionInfo().eventComment());
        }
        return testResult;
    }
}
