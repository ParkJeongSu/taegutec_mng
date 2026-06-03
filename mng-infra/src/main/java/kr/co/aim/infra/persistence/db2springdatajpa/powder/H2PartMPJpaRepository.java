package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.H2PartMPEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public interface H2PartMPJpaRepository extends JpaRepository<H2PartMPEntity, Long> {
    Page<H2PartMPEntity> findByIdocId(
            Long idocId,
            Pageable pageable
    );

    List<H2PartMPEntity> findByIdocId(Long idocId);
}
