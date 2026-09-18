package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserGroupMenuAuthJpaRepository extends JpaRepository<UserGroupMenuAuthEntity, Long> {

    List<UserGroupMenuAuthEntity> findByUserGroupId(Long userGroupId);

    Optional<UserGroupMenuAuthEntity> findByUserGroupIdAndMenuId(Long userGroupId, Long menuId);
}
