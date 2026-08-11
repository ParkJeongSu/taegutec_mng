package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.CarrierLotSearchCondition;
import kr.co.aim.common.condition.CarrierSearchCondition;
import kr.co.aim.common.condition.ProductionOrderHistorySearchCondition;
import kr.co.aim.common.dto.CarrierLotSearchResultDto;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.CarrierHistory;
import kr.co.aim.domain.model.ProductionOrderHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface CarrierRepository {
    /**
     * 사용자를 저장하거나 업데이트합니다.
     * @param carrier 저장할 사용자 도메인 객체
     * @return 저장된 사용자 도메인 객체 (ID 포함)
     */
    Carrier save(Carrier carrier);

    /**
     * ID로 사용자를 찾습니다.
     * @param id carrier ID
     * @return Optional<Carriers>
     */
    Optional<Carrier> findById(Long id);

    /**
     * carrierName로 사용자를 찾습니다.
     * @param carrierName carrierName
     * @return Optional<Carriers>
     */
    Optional<Carrier> findByCarrierName(String carrierName);

    /**
     * 모든 사용자를 찾습니다.
     * @return 모든 사용자 도메인 객체 리스트
     */
    List<Carrier> findAll();

    void deleteAllByIdInBatch(List<Long>ids);

    List<Carrier> findCarriersForEmptyContainer(
            String cleanState,
            String transportState,
            String transportJobId,
            String useState,
            Integer quantity,
            List<String> containerTypes
    );

    List<Carrier> findByQuantityAndCarrierType(
            BigDecimal quantity,
            String carrierType
    );

    Page<Carrier> findCarrierByCondition(CarrierSearchCondition condition, Pageable pageable);

    Page<CarrierLotSearchResultDto> findCarrierLotByCondition(CarrierLotSearchCondition condition, Pageable pageable);
}
