package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotJpaRepository extends JpaRepository<LotEntity, Long> {
    List<LotEntity> findByLotName(String lotName);

    List<LotEntity> findByLotNameAndProductionStatusNot(String lotName, String productionStatus);

    Optional<LotEntity> findByLotNameAndCarrierName(String lotName,String carrierName);
    Optional<LotEntity> findByCarrierName(String carrierName);
    List<LotEntity> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    List<LotEntity> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber,List<String> productionStatus);
    List<LotEntity> findByMngKey(Long mngKey);
    List<LotEntity> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(
            String mantiRequestState,
            LocalDateTime thresholdTime
    );

    @Query("SELECT lc FROM LotEntity lc " +
            "JOIN CarrierEntity c ON lc.carrierName = c.carrierName " +
            "JOIN CarrierDefEntity d ON c.carrierDefName = d.carrierDefName " +
            "WHERE lc.lotName = :lotName " +
            "AND d.carrierType = :carrierType "
            )
    List<LotEntity> findLotForUnpacking(
            @Param("lotName") String lotName,
            @Param("carrierType") String carrierType
    );

}