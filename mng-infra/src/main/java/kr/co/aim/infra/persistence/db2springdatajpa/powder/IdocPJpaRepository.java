package kr.co.aim.infra.persistence.db2springdatajpa.powder;

import kr.co.aim.infra.persistence.db2entity.powder.IdocPEntity;
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
public interface IdocPJpaRepository extends JpaRepository<IdocPEntity, Long> {
    // 상태값(STATE)으로 목록을 조회하는 쿼리 메소드 예시
    List<IdocPEntity> findByState(Integer state);
    Optional<IdocPEntity> findByLineId(Long lineId);
    Page<IdocPEntity> findAll(Pageable pageable);
    @Query("SELECT COALESCE(MAX(i.lineId), 0) FROM IdocPEntity i")
    Long findMaxLineId();
}