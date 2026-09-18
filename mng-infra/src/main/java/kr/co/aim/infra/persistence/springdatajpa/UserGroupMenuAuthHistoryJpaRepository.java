package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupMenuAuthHistoryJpaRepository extends JpaRepository<UserGroupMenuAuthHistoryEntity, Long> {
}
