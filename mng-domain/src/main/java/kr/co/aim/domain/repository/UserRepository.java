package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByFactoryNameAndUserId(String factoryName, String userId);

    List<User> findByFactoryName(String factoryName);

    User save(User user);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);
}
