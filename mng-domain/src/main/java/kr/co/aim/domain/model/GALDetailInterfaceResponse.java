package kr.co.aim.domain.model;
import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GALDetailInterfaceResponse {

    // === 공통 필드 ===
    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
    private String cClient;
    private String cOrderId;
    private String cOrderTy;

    // === Insert 전용 상세 필드 ===
    private String dataCode;
    private Long cOrderLn;
    private String cCoId;
    private String cCoTy;
    private String cZone;
    private String cDrivingProfile;

    // === Powder 전용 상세 필드 ===
    private Integer rrn;
    private Integer lineNo;
    private String cPartId;
    private Integer lot;
    private BigDecimal qty;
    private String uom;
    private String machine;
    private Integer currRrn;
    private Integer nextRrn;
    private String galKey;
    private Integer refLot;
    private Integer cmoord;
    private Long mngKey;

}
