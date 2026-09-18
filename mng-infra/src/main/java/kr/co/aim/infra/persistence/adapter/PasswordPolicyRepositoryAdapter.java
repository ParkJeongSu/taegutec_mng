package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.PasswordPolicy;
import kr.co.aim.domain.repository.PasswordPolicyRepository;
import kr.co.aim.infra.persistence.entity.PasswordPolicyEntity;
import kr.co.aim.infra.persistence.mapper.PasswordPolicyMapper;
import kr.co.aim.infra.persistence.springdatajpa.PasswordPolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PasswordPolicyRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 PasswordPolicyJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class PasswordPolicyRepositoryAdapter implements PasswordPolicyRepository {

    private final PasswordPolicyJpaRepository passwordPolicyJpaRepository;
    private final PasswordPolicyMapper passwordPolicyMapper;

    @Override
    public List<PasswordPolicy> findAll() {
        List<PasswordPolicyEntity> entities = passwordPolicyJpaRepository.findAll();
        List<PasswordPolicy> result = new ArrayList<>();
        for (PasswordPolicyEntity entity : entities) {
            result.add(passwordPolicyMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<PasswordPolicy> findById(Long id) {
        Optional<PasswordPolicyEntity> entityOptional = passwordPolicyJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(passwordPolicyMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<PasswordPolicy> findByFactoryName(String factoryName) {
        Optional<PasswordPolicyEntity> entityOptional = passwordPolicyJpaRepository.findByFactoryName(factoryName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(passwordPolicyMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<PasswordPolicy> findByFactoryNameAndPolicyName(String factoryName, String policyName) {
        Optional<PasswordPolicyEntity> entityOptional = passwordPolicyJpaRepository.findByFactoryNameAndPolicyName(factoryName, policyName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(passwordPolicyMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public PasswordPolicy save(PasswordPolicy passwordPolicy) {
        PasswordPolicyEntity entity = passwordPolicyMapper.toEntity(passwordPolicy);
        PasswordPolicyEntity savedEntity = passwordPolicyJpaRepository.save(entity);
        return passwordPolicyMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        passwordPolicyJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        passwordPolicyJpaRepository.deleteAllByIdInBatch(ids);
    }
}
