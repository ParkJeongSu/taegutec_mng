package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.UserGroupMember;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserGroupMemberRepository {

    List<UserGroupMember> findAll();

    Optional<UserGroupMember> findById(Long id);

    List<UserGroupMember> findByUserId(Long userId);

    List<UserGroupMember> findByUserGroupId(Long userGroupId);

    Optional<UserGroupMember> findByUserGroupIdAndUserId(Long userGroupId, Long userId);

    UserGroupMember save(UserGroupMember userGroupMember);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<UserGroupMember> findUserGroupMembersWithDetails(Long userId, Long userGroupId, String factoryName, Pageable pageable);
}

