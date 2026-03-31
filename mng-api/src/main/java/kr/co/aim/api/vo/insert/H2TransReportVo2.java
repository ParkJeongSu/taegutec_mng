package kr.co.aim.api.vo.insert;

import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class H2TransReportVo2 {
    private String transportJobName;
    private String carrierName;
    private String messageName;
    private Port port;
    private PortDef portDef;
}
