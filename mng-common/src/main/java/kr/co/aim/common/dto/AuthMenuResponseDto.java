package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class AuthMenuResponseDto {
    private Long id;
    private Long authorityId;
    private String authorityName;
    private Long systemDefId;
    private String systemDefName;
    private Long menuId;
    private String menuName;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;

    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection // ✨ 이 어노테이션이 있어야 QUserResponseDto가 생성됩니다.
    public AuthMenuResponseDto(    Long id,
                                   Long authorityId,
                                   String authorityName,
                                   Long systemDefId,
                                   String systemDefName,
                                   Long menuId,
                                   String menuName,
                                   String checkOutState,
                                   LocalDateTime checkOutTime,
                                   String checkOutUser,
                                   String dataState,
                                   String eventName,
                                   
                                   LocalDateTime eventTime,
                                   String eventUser,
                                   String eventComment) {
        this.id = id;
        this.authorityId = authorityId;
        this.authorityName = authorityName;
        this.systemDefId = systemDefId;
        this.systemDefName = systemDefName;
        this.menuId = menuId;
        this.menuName = menuName;
        this.checkOutState = checkOutState;
        this.checkOutTime = checkOutTime;
        this.checkOutUser = checkOutUser;
        this.dataState = dataState;
        this.eventName = eventName;
        
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
    }

}