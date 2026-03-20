package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.IdocEntity;
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
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
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

    @Query("SELECT COALESCE(MAX(i.lineId), 0) FROM IdocEntity i")
    Long findMaxLineId();
}