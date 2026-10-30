package kr.co.aim.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GALH2TransInterfaceResponse {

    // ==========================================
    // 1. 공통 필드 (Common - Insert & Powder)
    // ==========================================
    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Long modCnt;          // H2TransEntity: Integer, H2TransPEntity: Long -> 호환성을 위해 Long 적용
    private Long cTransTy;
    private String cOrderId;

    // ==========================================
    // 2. Insert 전용 필드 (Insert Only - H2TRANS)
    // ==========================================
    private String cClient;
    private String cOrderTy;
    private String dataCode;
    private Long cErrId;
    private String cText1;
    private String cTCode;
    private Long cOrderLn;
    private String cGalId;
    private String cGalWhs;
    private String cCoId;
    private String cCoTy;
    private BigDecimal cGrWgAct;
    private String cReqZone;
    private String cZone;
    private String cLocId;
    private String cErrDsc;
    private String cWcId;
    private String cGeoCl;
    private String cUsrId;
    private Integer parsegId;
    private String parsegName;
    private String cDrivingProfile;

    // ==========================================
    // 3. Powder 전용 필드 (Powder Only - H2TRANSP)
    // ==========================================
    private Integer rrn;
    private Integer lineNo;
    private Integer lot;
    private String galKey;
    private String carrierId;
    private Integer currRrn;
    private Integer nextRrn;
    private BigDecimal actQty;
    private BigDecimal missQty;
    private BigDecimal surpQty;
    private String resultStat;
    private String errReason;
    private LocalDateTime eventDt;
    private String cPartId;
    private Long mngKey;

    // --- 기타 Powder 비즈니스 로직 확장 필드 ---
    private BigDecimal qty;
    private String uom;
    private String machine;
    private Integer refLot;
    private Integer cmoord;
}