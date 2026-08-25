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

    // === 공통 필드 ===
    private Long lineId;
    private Long idocId;
    private Long idocTypId;
    private Long state;
    private Long errorCode;
    private Long source;
    private Long destination;
    private Long tidId;
    private String docNum;
    private String queueName;
    private String partnerType;
    private String partnerName;
    private String partnerPort;
    private String msgVariant;
    private String arcKey;
    private LocalDateTime dtimeCre;
    private LocalDateTime dtimeMod;
    private String usrMod;
    private String pgmMod;
    private Long modCnt; // Long으로 통일

    // === Insert 전용 필드 ===
    private String dataCode;
    private String bookCtrl;
    private String cClient;
    private String cOrderId;
    private String cOrderTy;
    private String cDtPick;
    private Integer cOrderPrio;
    private String cTCode;
    private String cLocId;
    private String cWcId;
    private String cGalId;
    private String cGalWhs;
    private String cHostUsr;
    private String cUsrNo;
    private Long cTransTy;
    private Long cErrId;
    private String cText1;
    private Long cOrderLn;
    private String cCoId;
    private BigDecimal cGrWgAct;
    private String cReqZone;
    private String cZone;
    private String cErrDsc;

    // === Powder 전용 필드 ===
    private String fromWhCd;
    private String toWhCd;
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

}
