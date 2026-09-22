package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.UserGroupMenuAuth;
import kr.co.aim.domain.repository.UserGroupMenuAuthRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMenuAuthMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserGroupMenuAuthJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserGroupMenuAuthRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserGroupMenuAuthJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserGroupMenuAuthRepositoryImpl implements UserGroupMenuAuthRepository {

    private final UserGroupMenuAuthJpaRepository userGroupMenuAuthJpaRepository;
    private final UserGroupMenuAuthMapper userGroupMenuAuthMapper;

    @Override
    public List<UserGroupMenuAuth> findAll() {
        List<UserGroupMenuAuthEntity> entities = userGroupMenuAuthJpaRepository.findAll();
        List<UserGroupMenuAuth> result = new ArrayList<>();
        for (UserGroupMenuAuthEntity entity : entities) {
            result.add(userGroupMenuAuthMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMenuAuth> findById(Long id) {
        Optional<UserGroupMenuAuthEntity> entityOptional = userGroupMenuAuthJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMenuAuthMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<UserGroupMenuAuth> findByUserGroupId(Long userGroupId) {
        List<UserGroupMenuAuthEntity> entities = userGroupMenuAuthJpaRepository.findByUserGroupId(userGroupId);
        List<UserGroupMenuAuth> result = new ArrayList<>();
        for (UserGroupMenuAuthEntity entity : entities) {
            result.add(userGroupMenuAuthMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMenuAuth> findByUserGroupIdAndMenuId(Long userGroupId, Long menuId) {
        Optional<UserGroupMenuAuthEntity> entityOptional = userGroupMenuAuthJpaRepository.findByUserGroupIdAndMenuId(userGroupId, menuId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMenuAuthMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public UserGroupMenuAuth save(UserGroupMenuAuth userGroupMenuAuth) {
        UserGroupMenuAuthEntity entity = userGroupMenuAuthMapper.toEntity(userGroupMenuAuth);
        UserGroupMenuAuthEntity savedEntity = userGroupMenuAuthJpaRepository.save(entity);
        return userGroupMenuAuthMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userGroupMenuAuthJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userGroupMenuAuthJpaRepository.deleteAllByIdInBatch(ids);
    }
}
