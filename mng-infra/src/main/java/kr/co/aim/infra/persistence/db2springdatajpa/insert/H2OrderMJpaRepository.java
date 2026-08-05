package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public interface H2OrderMJpaRepository extends JpaRepository<H2OrderMEntity, Long> {
    Optional<H2OrderMEntity> findByLineId(Long lineId);
    List<H2OrderMEntity> findByIdocId(Long idocId);
    Page<H2OrderMEntity> findByIdocId(Long idocId,Pageable pageable);
    @Query("SELECT h FROM H2OrderMEntity h WHERE h.cOrderId = :cOrderId")
    List<H2OrderMEntity> findByCOrderId(String cOrderId);

    @Query("SELECT COALESCE(MAX(CAST(m.cOrderId AS Long)), 0L) + 1L FROM H2OrderMEntity m")
    Long findMaxOrderId();

    @Query("SELECT COALESCE(MAX(m.lineId), 0) +1 FROM H2OrderMEntity m ")
    Long findMaxLineId();
}
