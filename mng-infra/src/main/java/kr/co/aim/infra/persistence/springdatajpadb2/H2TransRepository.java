package kr.co.aim.infra.persistence.springdatajpadb2;

import kr.co.aim.infra.persistence.entitydb2.H2TransEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface H2TransRepository extends JpaRepository<H2TransEntity, Long> {
    // 특정 트랜잭션 타입(TRANSTYPE)으로 조회
    List<H2TransEntity> findByTransType(Integer transType);
    Optional<H2TransEntity> findBylineId(Long lineId);
}
