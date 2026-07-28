package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.TransportOrderSearchCondition;
import kr.co.aim.domain.model.TransportOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 사용자 저장소의 기능을 정의하는 인터페이스.
 * 애플리케이션의 다른 부분(서비스 계층 등)은 이 인터페이스에만 의존합니다.
 * 실제 구현 기술(JPA, JDBC 등)과는 완전히 분리됩니다.
 */
public interface TransportOrderRepository {

    TransportOrder save(TransportOrder transportOrder);

    Optional<TransportOrder> findById(Long id);

    List<TransportOrder> findByTransportTypeInAndTransportStatus(List<String> types, String status);

    Optional<TransportOrder> findWithLockById(Long id);

    Optional<TransportOrder> findByTransportOrderId(String transportOrderId);

    List<TransportOrder> findTransportOrderByCondition(
            String carrierName,
            String transportType,
            List<String> transportStatus
    );

    List<TransportOrder> findOutboundOrderForTransportRequest(
            String transportType,
            String transportStatus,
            String workStationId
    );

    List<TransportOrder> findOutboundOrderForTransportRequest(
            String transportType,
            List<String> transportStatus,
            String workStationId
    );

    Page<TransportOrder> findTransportOrderWithConditions(TransportOrderSearchCondition condition, Pageable pageable);

}
