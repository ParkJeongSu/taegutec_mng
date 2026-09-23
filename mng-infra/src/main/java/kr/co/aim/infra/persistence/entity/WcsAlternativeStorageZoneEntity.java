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
@IdClass(WcsAlternativeStorageZoneId.class)
@Table(name = "ALTERNATIVESTORAGEZONE", catalog = "NEXBEWCS", schema = "dbo")
public class WcsAlternativeStorageZoneEntity {

    @Id
    @Column(name = "alternativeZoneName", length = 30)
    private String alternativeZoneName;

    @Id
    @Column(name = "factoryName", length = 30)
    private String factoryName;

    @Id
    @Column(name = "priority")
    private Integer priority;

    @Id
    @Column(name = "sourceZoneName", length = 30)
    private String sourceZoneName;

    @Column(name = "description", length = 255)
    private String description;

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
