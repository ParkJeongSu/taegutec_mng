package kr.co.aim.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@IdClass(WcsSubTransferRuleId.class)
@Table(name = "SUB_TRANSFER_RULE", catalog = "NEXBEWCS", schema = "dbo")
public class WcsSubTransferRuleEntity {

    @Id
    @Column(name = "equipmentName", length = 30)
    private String equipmentName;

    @Id
    @Column(name = "factoryName", length = 30)
    private String factoryName;

    @Id
    @Column(name = "moduleName", length = 30)
    private String moduleName;

    @Id
    @Column(name = "routeLinkId")
    private Long routeLinkId;

    @Column(name = "carrierCount")
    private Integer carrierCount;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "moduleType", length = 30)
    private String moduleType;

    @Column(name = "ngStatus", length = 30)
    private String ngStatus;

    @Column(name = "lastEventComment", length = 255)
    private String lastEventComment;

    @Column(name = "lastEventName", length = 50)
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser", length = 30)
    private String lastEventUser;
}
