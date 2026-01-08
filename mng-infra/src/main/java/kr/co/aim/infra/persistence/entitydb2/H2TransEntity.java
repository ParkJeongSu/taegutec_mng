package kr.co.aim.infra.persistence.entitydb2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "H2TRANS")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
public class H2TransEntity {
    @Id
    @Column(name = "LINEID")
    private Long lineId;

    @Column(name = "TRANSTYPE")
    private Integer transType;

    @Column(name = "\"ORDER\"") // 예약어 처리
    private String order;

    @Column(name = "RRN")
    private Long rrn;

    @Column(name = "QTY")
    private BigDecimal qty;

}
