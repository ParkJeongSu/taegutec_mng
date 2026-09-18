package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.UserGroupMember;

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
}
