package kr.co.aim.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import kr.co.aim.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
public class UserResponseDto {
    private Long id;
    private String userId;
    private Long authorityId;
    private String authorityName;
    private String userName;
    private String password;
    private String email;
    private String phone1;
    private String phone2;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static UserResponseDto from (User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .authorityId(user.getAuthorityId())
                .userName(user.getUserName())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone1(user.getPhone1())
                .phone2(user.getPhone2())
                .checkOutState(user.getCheckOutState())
                .checkOutTime(user.getCheckOutTime())
                .checkOutUser(user.getCheckOutUser())
                .dataState(user.getDataState())
                .eventName(user.getEventName())
                .eventTime(user.getEventTime())
                .eventUser(user.getEventUser())
                .eventComment(user.getEventComment())
                .build();
    }

    @QueryProjection // ✨ 이 어노테이션이 있어야 QUserResponseDto가 생성됩니다.
    public UserResponseDto(Long id,
                           String userId,
                           Long authorityId,
                           String authorityName,
                           String userName,
                           String password,
                           String email,
                           String phone1,
                           String phone2,
                           String checkOutState,
                           LocalDateTime checkOutTime,
                           String checkOutUser,
                           String dataState,
                           String eventName,
                           LocalDateTime eventTime,
                           String eventUser,
                           String eventComment) {
        this.id = id;
        this.userId = userId;
        this.authorityId = authorityId;
        this.authorityName = authorityName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone1 = phone1;
        this.phone2 = phone2;
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