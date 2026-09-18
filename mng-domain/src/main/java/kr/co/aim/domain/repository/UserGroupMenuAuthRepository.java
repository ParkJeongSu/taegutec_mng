package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.UserGroupMenuAuth;

import java.util.List;
import java.util.Optional;

public interface UserGroupMenuAuthRepository {

    List<UserGroupMenuAuth> findAll();

    Optional<UserGroupMenuAuth> findById(Long id);

    List<UserGroupMenuAuth> findByUserGroupId(Long userGroupId);

    Optional<UserGroupMenuAuth> findByUserGroupIdAndMenuId(Long userGroupId, Long menuId);

    UserGroupMenuAuth save(UserGroupMenuAuth userGroupMenuAuth);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
