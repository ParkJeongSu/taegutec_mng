package kr.co.aim.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
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
    private String authSelect;
    private String authSave;
    private String authDelete;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
}
