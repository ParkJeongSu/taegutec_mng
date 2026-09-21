package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupMember implements HasTransactionInfo {
    private Long id;
    private String factoryName;
    private Long userId;
    private String employeeId;
    private String userName;
    private Long userGroupId;
    private String userGroupName;
    private String groupDescription;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}

