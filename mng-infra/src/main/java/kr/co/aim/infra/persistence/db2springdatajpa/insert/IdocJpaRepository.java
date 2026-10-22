package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public interface IdocJpaRepository extends JpaRepository<IdocEntity, Long> {
    // 상태값(STATE)으로 목록을 조회하는 쿼리 메소드 예시
    List<IdocEntity> findByState(Integer state);
    Optional<IdocEntity> findByLineId(Long lineId);
    Page<IdocEntity> findAll(Pageable pageable);
    // IDOC과 H2ORDERM을 조인하여 주문 유형(orderTy)에 따른 IDOC 목록 조회
    @Query("SELECT i FROM IdocEntity i " +
            "JOIN H2OrderMEntity m ON i.lineId = m.idocId " +
            "WHERE m.cOrderTy = :orderTy")
    Page<IdocEntity> findIdocsByOrderType(@Param("orderTy") String orderTy, Pageable pageable);

    @Query("SELECT i FROM IdocEntity i " +
            "WHERE i.idocTypId IN :idocTypIds " +
            "AND i.errorCode = :errorCode "
    )
    List<IdocEntity> findByIdocTypIdsAndErrorCode(
            @Param("idocTypIds") List<Long> idocTypIds,
            @Param("errorCode") Integer errorCode);

    @Query("SELECT i FROM IdocEntity i " +
            "WHERE i.idocTypId IN :idocTypIds " +
            "AND i.state = :state " +
            "AND i.errorCode = :errorCode " +
            "ORDER BY i.dtimeCre ASC"
    )
    List<IdocEntity> findByIdocTypIdsAndStateAndErrorCode(
            @Param("idocTypIds") List<Long> idocTypIds,
            @Param("state") Integer state,
            @Param("errorCode") Integer errorCode
    );

    /**
     * 지정된 범위(:minId ~ :maxId) 내에서
     * 바로 다음 번호가 아직 사용되지 않은 가장 큰 번호 + 1을 채번
     */
    @Query("SELECT COALESCE(MAX(a.lineId + 1), :minId) FROM IdocEntity a " +
            "WHERE a.lineId >= :minId AND a.lineId < :maxId " +
            "  AND NOT EXISTS (" +
            "      SELECT 1 FROM IdocEntity b " +
            "      WHERE b.lineId = a.lineId + 1" +
            "  )")
    Long findNextAvailableLineId(@Param("minId") Long minId, @Param("maxId") Long maxId);

    // 1) 내가 사용하는 채번 (1 ~ 499,999,999)
    default Long findMaxLineId() {
        return findNextAvailableLineId(1L, 499999999L);
    }

    // 2) 상대편 시스템 채번 대역 (500,000,000 ~ 999,999,999)
    default Long findMaxLineIdForCreateOrder() {
        return findNextAvailableLineId(500000000L, 999999999L);
    }

    @Query("SELECT COUNT(i) > 0 FROM IdocEntity i WHERE i.lineId = 1")
    boolean existsByLineIdOne();

    // 2. 존재하는 데이터들의 바로 다음 빈틈 중 가장 작은 값 탐색
    @Query("SELECT COALESCE(MIN(i.lineId), 0) + 1 FROM IdocEntity i " +
            "WHERE i.lineId BETWEEN 1 AND 499999998 " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM IdocEntity sub " +
            "    WHERE sub.lineId = i.lineId + 1" +
            ")")
    Long findMinAvailableLineIdAfterOne();
}