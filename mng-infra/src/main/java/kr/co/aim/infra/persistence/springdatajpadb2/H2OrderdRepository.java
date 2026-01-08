package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.H2OrderdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface H2OrderdRepository extends JpaRepository<H2OrderdEntity, Long> {
    // 특정 주문번호("ORDER")로 상세 내역 조회
    List<H2OrderdEntity> findByOrder(String order);
    Optional<H2OrderdEntity> findBylineId(Long lineId);
}
