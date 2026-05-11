package kr.co.aim.api.strategy;

import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FactoryGALInterfaceStrategy {
    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable);
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable);
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable);
}
