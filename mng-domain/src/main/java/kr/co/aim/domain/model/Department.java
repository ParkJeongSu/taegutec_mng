package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.DepartmentCreateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Department implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private String departmentName;
    private String useState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Department create(DepartmentCreateCommand command) {
        return Department.builder()
                .id(TsidUtils.nextId())
                .factoryName(command.getFactoryName())
                .departmentName(command.getDepartmentName())
                .useState(command.getUseState())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();

    }
}
