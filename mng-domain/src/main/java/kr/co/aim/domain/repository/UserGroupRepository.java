package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.UserGroup;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository {

    List<UserGroup> findAll();

    Optional<UserGroup> findById(Long id);

    Optional<UserGroup> findByFactoryNameAndUserGroupName(String factoryName, String userGroupName);

    List<UserGroup> findByFactoryName(String factoryName);

    UserGroup save(UserGroup userGroup);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
