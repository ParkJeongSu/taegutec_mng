package kr.co.aim.api.vo.powder.sim;

import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDPEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMPEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class H2OrderDetailVo {
    private final H2OrderMPEntity master;
    private final H2OrderDPEntity detail;
}