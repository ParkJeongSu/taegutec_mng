package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.PasswordPolicy;

import java.util.List;
import java.util.Optional;

public interface PasswordPolicyRepository {

    List<PasswordPolicy> findAll();

    Optional<PasswordPolicy> findById(Long id);

    Optional<PasswordPolicy> findByFactoryName(String factoryName);

    Optional<PasswordPolicy> findByFactoryNameAndPolicyName(String factoryName, String policyName);

    PasswordPolicy save(PasswordPolicy passwordPolicy);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
