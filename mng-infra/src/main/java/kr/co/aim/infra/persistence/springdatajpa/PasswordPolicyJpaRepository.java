package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.PasswordPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordPolicyJpaRepository extends JpaRepository<PasswordPolicyEntity, Long> {

    Optional<PasswordPolicyEntity> findByFactoryName(String factoryName);

    Optional<PasswordPolicyEntity> findByFactoryNameAndPolicyName(String factoryName, String policyName);
}
