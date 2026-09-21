package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.SysUser;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<SysUser> findAll();

    Optional<SysUser> findById(Long id);

    Optional<SysUser> findByFactoryNameAndUserId(String factoryName, String userId);

    List<SysUser> findByFactoryName(String factoryName);

    SysUser save(SysUser sysUser);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
