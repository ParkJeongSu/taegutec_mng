package kr.co.aim.api.dto;

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
public class AuthorityResponseDto {
    private Long id;
    private String authorityName;
    private String description;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;

    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public AuthorityResponseDto(Long id,
                                String authorityName,
                                String description,
                                String checkOutState,
                                LocalDateTime checkOutTime,
                                String checkOutUser,
                                String dataState,
                                String eventName,
                                
                                LocalDateTime eventTime,
                                String eventUser,
                                String eventComment)
    {
        this.id = id;
        this.authorityName = authorityName;
        this.description = description;
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
