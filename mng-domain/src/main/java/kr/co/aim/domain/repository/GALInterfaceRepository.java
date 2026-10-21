package kr.co.aim.domain.repository;

import kr.co.aim.domain.repository.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.domain.repository.condition.GALInterfaceSearchCondition;
import kr.co.aim.domain.repository.condition.GALPartSearchCondition;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface GALInterfaceRepository {

    public Page<GALInterfaceResponse> getInterfaceList(GALInterfaceSearchCondition condition, Pageable pageable);
    public Page<GALDetailInterfaceResponse> getDetailInterfaceList(GALDetailInterfaceSearchCondition condition, Pageable pageable);
    public Page<GALPartResponse> getPartList(GALPartSearchCondition condition, Pageable pageable);
}
