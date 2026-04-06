package kr.co.aim.api.vo.insert.ops;

import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class InsertEventQueueReportVo {

    private final String transportJobName;
    private final String messageName;
    private final Optional<Port> optionalPort;
    private final Optional<PortDef> optionalPortDef;
    private final String carrierName;
    private final String actualZoneName;
    // 측정 무게
    private final String actualWeight;
    private final String actualRackLocationId;
    // 3. 예외 처리 정보
    private final List<String> errorTexts;
    private final TransactionInfo tx;
    // orderType
    // I : inbound
    // O : outbound
    // R : relocation
    private final String orderType;
    // requestSource : job 의 구분
    // GAL : Gal 로 부터 시작된 반송
    // WCS : WCS 내부에서 Relocation 등으로 시작한 반송
    private final String requestSource;

}
