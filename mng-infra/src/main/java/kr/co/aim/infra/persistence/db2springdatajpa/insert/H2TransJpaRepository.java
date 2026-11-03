package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import kr.co.aim.infra.persistence.db2entity.insert.H2TransEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public interface H2TransJpaRepository extends JpaRepository<H2TransEntity, Long> {

    // 1. IN 절 존재 여부 확인 (@Query 사용으로 CTransTy 파싱 에러 완벽 방지)
    @Query("SELECT COUNT(h) > 0 FROM H2TransEntity h WHERE h.cTransTy IN :cTransTys AND h.cOrderId = :cOrderId")
    boolean existsByCTransTyInAndCOrderId(@Param("cTransTys") Collection<Long> cTransTys, @Param("cOrderId") String cOrderId);

    // 2. 단건 존재 여부 확인
    @Query("SELECT COUNT(h) > 0 FROM H2TransEntity h WHERE h.cTransTy = :cTransTy AND h.cOrderId = :cOrderId")
    boolean existsByCTransTyAndCOrderId(@Param("cTransTy") Long cTransTy, @Param("cOrderId") String cOrderId);

    // 3. 최신 1건 조회 (Hibernate 6 / Spring Boot 3 환경에서는 JPQL 내 FETCH FIRST 1 ROWS ONLY 지원)
    @Query("SELECT h FROM H2TransEntity h WHERE h.cTransTy = :cTransTy AND h.cOrderId = :cOrderId ORDER BY h.dtimeCre DESC FETCH FIRST 1 ROWS ONLY")
    Optional<H2TransEntity> findFirstByCTransTyAndCOrderIdOrderByDtimeCreDesc(@Param("cTransTy") Long cTransTy, @Param("cOrderId") String cOrderId);


    Optional<H2TransEntity> findByLineId(Long lineId);
    List<H2TransEntity> findByIdocId(Long idocId);
    Page<H2TransEntity> findByIdocId(Long idocId,Pageable pageable);

    @Query(
            value = "select h from H2TransEntity h where h.cOrderId = :cOrderId order by h.dtimeCre desc",
            countQuery = "select count(h) from H2TransEntity h where h.cOrderId = :cOrderId"
    )
    Page<H2TransEntity> selectByCOrderId(@Param("cOrderId") String cOrderId, Pageable pageable);

//    @Query("SELECT COALESCE(MAX(h.lineId), 0) +1 FROM H2TransEntity h " +
//            "WHERE h.lineId BETWEEN 1 AND 499999999"
//    )
//    Long findMaxLineId();

    // 1) 내가 사용하는 시퀀스 채번 (1 ~ 499,999,999 대역)
    @Query(value = "SELECT NEXT VALUE FOR AIM_C_TST.H2TRANS_L FROM SYSIBM.SYSDUMMY1", nativeQuery = true)
    Long findNextLineId();

    // 기존 호출부 호환용 default 메서드 (필요시)
    default Long findMaxLineId() {
        return findNextLineId();
    }

    @Query("SELECT COUNT(h) > 0 FROM H2TransEntity h WHERE h.lineId = 1")
    boolean existsByLineIdOne();

    // 2. 존재하는 데이터들의 바로 다음 빈틈 중 가장 작은 값 탐색
    @Query("SELECT COALESCE(MIN(h.lineId), 0) + 1 FROM H2TransEntity h " +
            "WHERE h.lineId BETWEEN 1 AND 499999998 " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM H2TransEntity sub " +
            "    WHERE sub.lineId = h.lineId + 1" +
            ")")
    Long findMinAvailableLineIdAfterOne();
}
