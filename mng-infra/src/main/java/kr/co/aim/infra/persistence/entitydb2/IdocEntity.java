package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "IDOC")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class IdocEntity {
    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "IDOCTYPE")
    private Long idocType;

    @Column(name = "STATE")
    private Integer state;

    @Column(name = "ERRORCODE")
    private Long errorCode;

    @Column(name = "SOURCE")
    private Long source;

    @Column(name = "DESTINATION")
    private Long destination;

}
