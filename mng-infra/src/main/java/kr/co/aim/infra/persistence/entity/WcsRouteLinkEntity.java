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
@IdClass(WcsRouteLinkId.class)
@Table(name = "ROUTE_LINK", catalog = "NEXBEWCS", schema = "dbo")
public class WcsRouteLinkEntity {

    @Id
    @Column(name = "factoryName", length = 30)
    private String factoryName;

    @Id
    @Column(name = "routeLinkId")
    private Long routeLinkId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "fromNodeId")
    private Long fromNodeId;

    @Column(name = "[length]")
    private Integer length;

    @Column(name = "passYn", length = 1)
    private String passYn;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "processType", length = 30)
    private String processType;

    @Column(name = "routeLinkType", length = 30)
    private String routeLinkType;

    @Column(name = "toNodeId")
    private Long toNodeId;

    @Column(name = "usableYn", length = 1)
    private String usableYn;

    @Column(name = "useYn", length = 1)
    private String useYn;

    @Column(name = "lastEventComment", length = 255)
    private String lastEventComment;

    @Column(name = "lastEventName", length = 50)
    private String lastEventName;

    @Column(name = "lastEventTime")
    private LocalDateTime lastEventTime;

    @Column(name = "lastEventUser", length = 30)
    private String lastEventUser;
}
