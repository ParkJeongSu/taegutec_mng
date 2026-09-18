package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.PasswordPolicyHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordPolicyHistoryJpaRepository extends JpaRepository<PasswordPolicyHistoryEntity, Long> {
}
