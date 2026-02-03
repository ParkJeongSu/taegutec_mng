package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdocJpaRepository extends JpaRepository<IdocEntity, Long> {
    // 상태값(STATE)으로 목록을 조회하는 쿼리 메소드 예시
    List<IdocEntity> findByState(Integer state);
    Optional<IdocEntity> findByLineId(Long lineId);
    Page<IdocEntity> findAll(Pageable pageable);
}