package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.SysUserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUserHistoryJpaRepository extends JpaRepository<SysUserHistoryEntity, Long> {
}
