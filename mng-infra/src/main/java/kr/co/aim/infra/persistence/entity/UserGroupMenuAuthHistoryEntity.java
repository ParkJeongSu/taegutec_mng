package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "USER_GROUP_MENU_AUTH_HISTORY", catalog = "NEXBEAUTH", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroupMenuAuthHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FACTORY_NAME")
    private String factoryName;

    @Column(name = "USER_GROUP_ID")
    private Long userGroupId;

    @Column(name = "MENU_ID")
    private Long menuId;

    @Column(name = "AUTH_SELECT")
    private String authSelect;

    @Column(name = "AUTH_SAVE")
    private String authSave;

    @Column(name = "AUTH_DELETE")
    private String authDelete;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
