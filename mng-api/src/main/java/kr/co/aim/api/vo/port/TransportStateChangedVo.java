package kr.co.aim.api.vo.port;

import kr.co.aim.common.enums.PortTransportState;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.model.Port;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class TransportStateChangedVo {
    private final Port port;
    private final PortTransportState portTransportState;
    private final TransactionInfo tx;
}