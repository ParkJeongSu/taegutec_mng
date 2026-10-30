package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class GALH2TransInterfaceSearchCondition {

    // ==========================================
    // 1. 공통 검색 조건 (Common - Insert & Powder)
    // ==========================================
    private Long lineId;
    private Long idocId;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;
    private Long cTransTy;
    private String cOrderId;

    // ==========================================
    // 2. Insert 전용 검색 조건 (Insert Only - H2TRANS)
    // ==========================================
    private String dataCode;
    private String cClient;
    private String cOrderTy;
    private Long cErrId;
    private String cText1;
    private String cTCode;
    private Long cOrderLn;
    private String cGalId;
    private String cGalWhs;
    private String cCoId;
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

    // ==========================================
    // 3. Powder 전용 검색 조건 (Powder Only - H2TRANSP)
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

    // --- Powder 수량 및 비즈니스 범위 검색 조건 ---
    private BigDecimal qty;
    private String uom;
    private String machine;
    private BigDecimal minReceiveQty;
    private BigDecimal maxReceiveQty;
    private BigDecimal defaultReceiveQty;
    private Long h2trnLineid;
}