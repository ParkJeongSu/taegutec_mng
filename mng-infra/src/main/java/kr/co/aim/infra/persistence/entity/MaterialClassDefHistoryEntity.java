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
@Table(name = "MATERIAL_CLASS_DEF_HISTORY", catalog = "NEXBEDEF", schema = "dbo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialClassDefHistoryEntity implements IBaseHistoryEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "MATERIAL_CLASS_DEF_NAME")
    private String materialClassDefName;

    @Column(name = "MATERIAL_CLASS_TYPE")
    private String materialClassType;

    @Column(name = "PARENT_CLASS_ID")
    private Long parentClassId;

    @Column(name = "CLASS_LEVEL")
    private Integer classLevel;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EVENT_NAME")
    private String eventName;

    @Column(name = "EVENT_TIME")
    private LocalDateTime eventTime;

    @Column(name = "EVENT_USER")
    private String eventUser;

    @Column(name = "EVENT_COMMENT")
    private String eventComment;
}
