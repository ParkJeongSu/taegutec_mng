package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupMemberJpaRepository extends JpaRepository<UserGroupMemberEntity, Long> {

    List<UserGroupMemberEntity> findByUserId(Long userId);

    List<UserGroupMemberEntity> findByUserGroupId(Long userGroupId);

    Optional<UserGroupMemberEntity> findByUserGroupIdAndUserId(Long userGroupId, Long userId);
}
