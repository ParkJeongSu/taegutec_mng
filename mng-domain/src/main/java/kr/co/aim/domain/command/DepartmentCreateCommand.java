package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class DepartmentCreateCommand {
    private final TransactionInfo transactionInfo;
    private final String factoryName;
    private final String departmentName;
    private final String useState;
}
