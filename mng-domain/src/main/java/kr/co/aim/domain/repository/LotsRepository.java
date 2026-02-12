package kr.co.aim.domain.repository;

import kr.co.aim.common.dto.LotsResponseDto;
import kr.co.aim.common.dto.LotsSearchConditionDto;
import kr.co.aim.domain.model.Lots;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface LotsRepository {
    /**
     * 사용자를 저장하거나 업데이트합니다.
     * @param carriers 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체 (ID 포함)
     */
    Lots save(Lots carriers);

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrier ID
     * @return Optional<Carriers>
     */
    Optional<Lots> findById(Long id);

    /**
     * lotName로 Lots를 찾습니다.
     * @param lotName lotName
     * @return Optional<Lots>
     */
    Optional<Lots> findByLotName(String lotName);

    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 사용자 도메인 객체 리스트
     */
    List<Lots> findAll();

    /**
     * carrierId로 List<Lots>를 찾습니다.
     * @param carrierId carrierId
     * @return List<Lots>
     */
    List<Lots> findByCarrierId(Long carrierId);

    void deleteAllByIdInBatch(List<Long>ids);

    Page<LotsResponseDto> findLotsWithConditions(LotsSearchConditionDto condition, Pageable pageable);
}
