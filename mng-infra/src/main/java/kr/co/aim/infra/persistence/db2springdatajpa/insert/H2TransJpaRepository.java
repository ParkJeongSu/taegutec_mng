package kr.co.aim.infra.persistence.db2springdatajpa.insert;

import kr.co.aim.infra.persistence.db2entity.insert.H2TransEntity;
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
public interface H2TransJpaRepository extends JpaRepository<H2TransEntity, Long> {
    Optional<H2TransEntity> findByLineId(Long lineId);
    List<H2TransEntity> findByIdocId(Long idocId);
    Page<H2TransEntity> findByIdocId(Long idocId,Pageable pageable);

    @Query(
            value = "select h from H2TransEntity h where h.cOrderId = :cOrderId order by h.dtimeCre desc",
            countQuery = "select count(h) from H2TransEntity h where h.cOrderId = :cOrderId"
    )
    Page<H2TransEntity> selectByCOrderId(@Param("cOrderId") String cOrderId, Pageable pageable);

    @Query("SELECT COALESCE(MAX(h.lineId), 0) +1 FROM H2TransEntity h")
    Long findMaxLineId();
}
