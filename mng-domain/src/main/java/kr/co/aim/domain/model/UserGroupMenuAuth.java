package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupMenuAuth implements HasTransactionInfo {

    private Long id;
    private String factoryName;
    private Long userGroupId;
    private Long menuId;
    private String authLevel;
    private String readable;
    private String writable;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
