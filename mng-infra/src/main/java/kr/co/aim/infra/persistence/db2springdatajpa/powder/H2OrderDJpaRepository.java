package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2OrderDEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public interface H2OrderDJpaRepository extends JpaRepository<H2OrderDEntity, Long> {
    Optional<H2OrderDEntity> findByLineId(Long lineId);
    List<H2OrderDEntity> findByIdocId(Long idocId);
    Page<H2OrderDEntity> findByIdocId(Long idocId,Pageable pageable);
    @Query("SELECT h FROM H2OrderDEntity h WHERE h.cOrderId = :cOrderId")
    List<H2OrderDEntity> findByCOrderId(String cOrderId);
}
