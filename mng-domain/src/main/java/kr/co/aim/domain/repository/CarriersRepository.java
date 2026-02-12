package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Carriers;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface CarriersRepository {
    /**
     * 사용자를 저장하거나 업데이트합니다.
     * @param carriers 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체 (ID 포함)
     */
    Carriers save(Carriers carriers);

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrier ID
     * @return Optional<Carriers>
     */
    Optional<Carriers> findById(Long id);

    /**
     * carrierName로 사용자를 찾습니다.
     * @param carrierName carrierName
     * @return Optional<Carriers>
     */
    Optional<Carriers> findByCarrierName(String carrierName);

    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 사용자 도메인 객체 리스트
     */
    List<Carriers> findAll();

//    Page<CarriersResponseDto> findCarriersWithConditions(CarriersSearchConditionDto condition, Pageable pageable);

    void deleteAllByIdInBatch(List<Long>ids);
}
