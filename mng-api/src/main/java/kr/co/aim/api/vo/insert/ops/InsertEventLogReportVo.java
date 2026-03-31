package kr.co.aim.api.vo.insert.ops;

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
    private final String locationCode;// TODO: actualId할지 고민
    // 측정 무게
    private final Long weight;
    // 3. 예외 처리 정보
    private final String errorText;
}
