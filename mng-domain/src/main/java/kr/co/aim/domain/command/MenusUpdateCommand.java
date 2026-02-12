package kr.co.aim.domain.command;

import kr.co.aim.common.record.TransactionInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class MenusUpdateCommand {
    private final TransactionInfo transactionInfo;
    private final Long id;
    private final Long systemDefId;
    private final String menuName;
    private final Long parentMenuId;
    private final String viewURL;
    private final Integer menuSEQ;
    private final String description;
    private final String iconName;
    private final String menuType;
    private final String checkOutState;
    private final LocalDateTime checkOutTime;
    private final String checkOutUser;
    private final String dataState;
}
