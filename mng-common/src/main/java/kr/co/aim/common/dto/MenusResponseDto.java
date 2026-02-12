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
public class MenusResponseDto {

    private Long id;
    private Long systemDefId;
    private String systemDefName;
    private String menuName;
    private Long parentMenuId;
    private String parentMenuName;
    private String viewURL;
    private Integer menuSEQ;
    private String description;
    private String iconName;
    private String menuType;
    private String checkOutState;
    private LocalDateTime checkOutTime;
    private String checkOutUser;
    private String dataState;
    private String eventName;

    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    @QueryProjection
    public MenusResponseDto(
            Long id,
            Long systemDefId,
            String systemDefName,
            String menuName,
            Long parentMenuId,
            String parentMenuName,
            String viewURL,
            Integer menuSEQ,
            String description,
            String iconName,
            String menuType,
            String checkOutState,
            LocalDateTime checkOutTime,
            String checkOutUser,
            String dataState,
            String eventName,
            
            LocalDateTime eventTime,
            String eventUser,
            String eventComment
    ){
        this.id= id;
        this.systemDefId= systemDefId;
        this.systemDefName= systemDefName;
        this.menuName= menuName;
        this.parentMenuId= parentMenuId;
        this.parentMenuName= parentMenuName;
        this.viewURL= viewURL;
        this.menuSEQ= menuSEQ;
        this.description= description;
        this.iconName= iconName;
        this.menuType= menuType;
        this.checkOutState= checkOutState;
        this.checkOutTime= checkOutTime;
        this.checkOutUser= checkOutUser;
        this.dataState= dataState;
        this.eventName= eventName;
        
        this.eventTime= eventTime;
        this.eventUser= eventUser;
        this.eventComment= eventComment;
    }
}