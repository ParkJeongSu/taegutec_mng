package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.SysUserEntity;
import kr.co.aim.infra.persistence.mapper.SysUserMapper;
import kr.co.aim.infra.persistence.springdatajpa.SysUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SysUserJpaRepository sysUserJpaRepository;
    private final SysUserMapper sysUserMapper;

    @Override
    public List<SysUser> findAll() {
        List<SysUserEntity> entities = sysUserJpaRepository.findAll();
        List<SysUser> result = new ArrayList<>();
        for (SysUserEntity entity : entities) {
            result.add(sysUserMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<SysUser> findById(Long id) {
        Optional<SysUserEntity> entityOptional = sysUserJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(sysUserMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<SysUser> findByFactoryNameAndUserId(String factoryName, String userId) {
        Optional<SysUserEntity> entityOptional = sysUserJpaRepository.findByFactoryNameAndUserId(factoryName, userId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(sysUserMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<SysUser> findByFactoryName(String factoryName) {
        List<SysUserEntity> entities = sysUserJpaRepository.findByFactoryName(factoryName);
        List<SysUser> result = new ArrayList<>();
        for (SysUserEntity entity : entities) {
            result.add(sysUserMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public SysUser save(SysUser sysUser) {
        SysUserEntity entity = sysUserMapper.toEntity(sysUser);
        SysUserEntity savedEntity = sysUserJpaRepository.save(entity);
        return sysUserMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        sysUserJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        sysUserJpaRepository.deleteAllByIdInBatch(ids);
    }
}
