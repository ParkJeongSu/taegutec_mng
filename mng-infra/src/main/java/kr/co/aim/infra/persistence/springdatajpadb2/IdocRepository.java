package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.IdocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IdocRepository extends JpaRepository<IdocEntity, Long> {
    // 상태값(STATE)으로 목록을 조회하는 쿼리 메소드 예시
    List<IdocEntity> findByState(Integer state);
    Optional<IdocEntity> findBylineId(Long lineId);
}