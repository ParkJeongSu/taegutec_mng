package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupMemberHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupMemberHistoryJpaRepository extends JpaRepository<UserGroupMemberHistoryEntity, Long> {
}
