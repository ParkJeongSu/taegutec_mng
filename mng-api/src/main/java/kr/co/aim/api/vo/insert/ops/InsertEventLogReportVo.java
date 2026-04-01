package kr.co.aim.api.vo.insert.ops;

import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InsertEventLogReportVo {

    private final String transportJobName;
    private final String messageName;
    private final Port port;
    private final PortDef portDef;
    private final String carrierName;
    private final String actualZone;
    // 측정 무게
    private final String actualWeight;
    private final String actualRackLocationId;
    // 3. 예외 처리 정보
    private final String errorText;
    private final TransactionInfo tx;
}
