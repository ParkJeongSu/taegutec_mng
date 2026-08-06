package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.LotCarrierMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LotCarrierMappingJpaRepository extends JpaRepository<LotCarrierMappingEntity, Long> {
    List<LotCarrierMappingEntity> findByLotName(String lotName);

    List<LotCarrierMappingEntity> findByLotNameAndProductionStatusNot(String lotName, String productionStatus);

    Optional<LotCarrierMappingEntity> findByLotNameAndCarrierName(String lotName,String carrierName);
    Optional<LotCarrierMappingEntity> findByCarrierName(String carrierName);
    List<LotCarrierMappingEntity> findByOrderIdAndOrderLineNumber(String orderId, String orderLineNumber);
    List<LotCarrierMappingEntity> findByOrderIdAndOrderLineNumberAndProductionStatusIn(String orderId, String orderLineNumber,List<String> productionStatus);
    List<LotCarrierMappingEntity> findByMngKey(Long mngKey);
    List<LotCarrierMappingEntity> findByMantiRequestStateAndMantiRequestTimeBeforeAndMantiReplyTimeIsNull(
            String mantiRequestState,
            LocalDateTime thresholdTime
    );

    @Query("SELECT lc FROM LotCarrierMappingEntity lc " +
            "JOIN CarrierEntity c ON lc.carrierName = c.carrierName " +
            "JOIN CarrierDefEntity d ON c.carrierDefName = d.carrierDefName " +
            "WHERE lc.lotName = :lotName " +
            "AND d.carrierType = :carrierType "
            )
    List<LotCarrierMappingEntity> findLotCarrierMappingForUnpacking(
            @Param("lotName") String lotName,
            @Param("carrierType") String carrierType
    );

}