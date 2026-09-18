package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.User;
import kr.co.aim.domain.repository.UserRepository;
import kr.co.aim.infra.persistence.entity.UserEntity;
import kr.co.aim.infra.persistence.mapper.UserMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserJpaRepository;
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

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        List<UserEntity> entities = userJpaRepository.findAll();
        List<User> result = new ArrayList<>();
        for (UserEntity entity : entities) {
            result.add(userMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<UserEntity> entityOptional = userJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByFactoryNameAndUserId(String factoryName, String userId) {
        Optional<UserEntity> entityOptional = userJpaRepository.findByFactoryNameAndUserId(factoryName, userId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByFactoryName(String factoryName) {
        List<UserEntity> entities = userJpaRepository.findByFactoryName(factoryName);
        List<User> result = new ArrayList<>();
        for (UserEntity entity : entities) {
            result.add(userMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userJpaRepository.deleteAllByIdInBatch(ids);
    }
}
