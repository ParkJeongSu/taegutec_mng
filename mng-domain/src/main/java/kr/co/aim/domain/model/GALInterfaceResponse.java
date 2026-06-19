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
public class GALInterfaceResponse {

    // === 공통 관리 필드 (Common Header) ===
    private Long lineId;
    private Long idocId;         // Powder의 lineid 또는 Insert의 idocId 대응
    private Integer idocTypId;
    private Integer state;
    private Integer errorCode;
    private Integer source;
    private Integer destination;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Integer modCnt;

    // === Insert 전용 IDOC 필드 ===
    private Long tidId;
    private String docNum;
    private String queueName;
    private String partnerType;
    private String partnerName;
    private String partnerPort;
    private String msgVariant;
    private String arcKey;

    // === Order Master 필드 (Insert & Powder 통합) ===
    private Long dataCode;
    private String cClient;
    private String cOrderId;
    private String cOrderTy;
    private Long bookCtrl;       // Insert 전용
    private String cDtPick;      // Insert 전용
    private Integer cOrderPrio;  // Insert 전용
    private String cTCode;
    private String cLocId;
    private String cWcId;
    private Long cGalId;
    private String cGalWhs;
    private String cHostUsr;
    private String cUsrNo;
    private String fromWhCd;     // Powder 전용
    private String toWhCd;       // Powder 전용

    // === Transaction / Result 필드 (Insert & Powder 통합) ===
    private Integer cTransTy;
    private Long cErrId;         // Insert 전용
    private String cText1;       // Insert 전용
    private Long cOrderLn;       // Insert 전용
    private Long cGaId;          // Insert 전용
    private String cCoId;        // Insert 전용
    private Long cGrWgAct;       // Insert 전용
    private String cReqZone;     // Insert 전용
    private String cZone;
    private String cErrDsc;      // Insert 전용
    private Integer rrn;         // Powder 전용 (공정순서)
    private Integer lineNo;      // Powder 전용
    private Integer lot;         // Powder 전용
    private String galKey;       // Powder 전용
    private String carrierId;    // Powder 전용
    private Integer currRrn;     // Powder 전용
    private Integer nextRrn;     // Powder 전용
    private BigDecimal actQty;   // Powder 전용
    private BigDecimal missQty;  // Powder 전용
    private BigDecimal surpQty;  // Powder 전용
    private String resultStat;   // Powder 전용
    private String errReason;    // Powder 전용
    private LocalDateTime eventDt; // Powder 전용
    private Long h2ordLineId;    // Powder 전용
    private Integer refLot;
    private Integer cmoord;
    private Long mngKey;

}
