package kr.co.aim.infra.persistence.adapter;

import kr.co.aim.domain.model.UserGroupMember;
import kr.co.aim.domain.repository.UserGroupMemberRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMemberEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMemberMapper;
import kr.co.aim.infra.persistence.springdatajpa.UserGroupMemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserGroupMemberRepository의 JPA 기반 어댑터 구현체.
 * 실제 DB 작업은 Spring Data JPA가 제공하는 UserGroupMemberJpaRepository에 위임합니다.
 */
@Repository
@RequiredArgsConstructor
public class UserGroupMemberRepositoryAdapter implements UserGroupMemberRepository {

    private final UserGroupMemberJpaRepository userGroupMemberJpaRepository;
    private final UserGroupMemberMapper userGroupMemberMapper;

    @Override
    public List<UserGroupMember> findAll() {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findAll();
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMember> findById(Long id) {
        Optional<UserGroupMemberEntity> entityOptional = userGroupMemberJpaRepository.findById(id);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMemberMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<UserGroupMember> findByUserId(Long userId) {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findByUserId(userId);
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public List<UserGroupMember> findByUserGroupId(Long userGroupId) {
        List<UserGroupMemberEntity> entities = userGroupMemberJpaRepository.findByUserGroupId(userGroupId);
        List<UserGroupMember> result = new ArrayList<>();
        for (UserGroupMemberEntity entity : entities) {
            result.add(userGroupMemberMapper.toDomain(entity));
        }
        return result;
    }

    @Override
    public Optional<UserGroupMember> findByUserGroupIdAndUserId(Long userGroupId, Long userId) {
        Optional<UserGroupMemberEntity> entityOptional = userGroupMemberJpaRepository.findByUserGroupIdAndUserId(userGroupId, userId);
        if (entityOptional.isPresent()) {
            return Optional.ofNullable(userGroupMemberMapper.toDomain(entityOptional.get()));
        }
        return Optional.empty();
    }

    @Override
    public UserGroupMember save(UserGroupMember userGroupMember) {
        UserGroupMemberEntity entity = userGroupMemberMapper.toEntity(userGroupMember);
        UserGroupMemberEntity savedEntity = userGroupMemberJpaRepository.save(entity);
        return userGroupMemberMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        userGroupMemberJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> ids) {
        userGroupMemberJpaRepository.deleteAllByIdInBatch(ids);
    }
}
