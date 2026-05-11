package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2TransPEntity;
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
public interface H2TransPJpaRepository extends JpaRepository<H2TransPEntity, Long> {
    Optional<H2TransPEntity> findByLineId(Long lineId);
    List<H2TransPEntity> findByIdocId(Long idocId);
    Page<H2TransPEntity> findByIdocId(Long idocId, Pageable pageable);
    @Query("SELECT COALESCE(MAX(h.lineId), 0) FROM H2TransPEntity h")
    Long findMaxLineId();
}
