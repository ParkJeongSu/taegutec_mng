package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupHistoryJpaRepository extends JpaRepository<UserGroupHistoryEntity, Long> {
}
