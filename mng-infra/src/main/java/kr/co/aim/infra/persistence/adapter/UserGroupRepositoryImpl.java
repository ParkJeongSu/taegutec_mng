package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.UserGroup;
import kr.co.aim.domain.repository.UserGroupRepository;
import kr.co.aim.infra.persistence.entity.UserGroupEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserGroupJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserGroupRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserGroupJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserGroupRepositoryImpl implements UserGroupRepository {

    private final UserGroupJpaRepository userGroupJpaRepository;
    private final UserGroupMapper userGroupMapper;

    @Override
    public List<UserGroup> findAll() {
        List<UserGroupEntity> entities = userGroupJpaRepository.findAll();
        List<UserGroup> result = new ArrayList<>();
        for (UserGroupEntity entity : entities) {
            result.add(userGroupMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroup> findById(Long id) {
        Optional<UserGroupEntity> entityOptional = userGroupJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserGroup> findByFactoryNameAndUserGroupName(String factoryName, String userGroupName) {
        Optional<UserGroupEntity> entityOptional = userGroupJpaRepository.findByFactoryNameAndUserGroupName(factoryName, userGroupName);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<UserGroup> findByFactoryName(String factoryName) {
        List<UserGroupEntity> entities = userGroupJpaRepository.findByFactoryName(factoryName);
        List<UserGroup> result = new ArrayList<>();
        for (UserGroupEntity entity : entities) {
            result.add(userGroupMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public UserGroup save(UserGroup userGroup) {
        UserGroupEntity entity = userGroupMapper.toEntity(userGroup);
        UserGroupEntity savedEntity = userGroupJpaRepository.save(entity);
        return userGroupMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userGroupJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userGroupJpaRepository.deleteAllByIdInBatch(ids);
    }
}
