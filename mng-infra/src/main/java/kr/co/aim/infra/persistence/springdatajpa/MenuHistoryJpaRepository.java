package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.MenuHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuHistoryJpaRepository extends JpaRepository<MenuHistoryEntity, Long> {
}
