package kr.co.aim.domain.model;

import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.MenusCreateCommand;
import kr.co.aim.domain.command.MenusUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Menus implements HasTransactionInfo {
    private Long id;
    private Long systemDefId;
    private String menuName;
    private Long parentMenuId;
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

    public static Menus create(MenusCreateCommand command){
        return Menus.builder()
                .menuName(command.getMenuName())
                .systemDefId(command.getSystemDefId())
                .menuSEQ(command.getMenuSEQ())
                .menuType(command.getMenuType())
                .description(command.getDescription())
                .parentMenuId(command.getParentMenuId())
                .iconName(command.getIconName())
                .viewURL(command.getViewURL())
                .eventName(command.getTransactionInfo().eventName())
                .eventTime(command.getTransactionInfo().eventTime())
                .eventUser(command.getTransactionInfo().eventUser())
                .eventComment(command.getTransactionInfo().eventComment())
                .build();

    }
    public void changeMenus(MenusUpdateCommand command){
        this.apply(command.getTransactionInfo());
        this.setMenuName(command.getMenuName());
        this.setDescription(command.getDescription());
        this.setMenuSEQ(command.getMenuSEQ());
        this.setMenuType(command.getMenuType());
        this.setIconName(command.getIconName());
        this.setSystemDefId(command.getSystemDefId());
        this.setParentMenuId(command.getParentMenuId());
        this.setViewURL(command.getViewURL());
    }
}
