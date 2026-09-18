package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHistoryJpaRepository extends JpaRepository<UserHistoryEntity, Long> {
}
