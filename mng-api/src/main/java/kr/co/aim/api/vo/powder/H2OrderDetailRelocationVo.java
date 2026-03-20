package kr.co.aim.api.vo.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.powder.H2OrderMEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class H2OrderDetailRelocationVo {
    private final H2OrderMEntity master;
    private final H2OrderDEntity source;
    private final H2OrderDEntity target;
}