package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.MaterialGroup;
import kr.co.aim.domain.repository.condition.MaterialGroupSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialGroupRepository {

    List<MaterialGroup> findAll();

    Optional<MaterialGroup> findById(Long id);

    Optional<MaterialGroup> findByMaterialGroupDefName(String materialGroupDefName);

    MaterialGroup save(MaterialGroup materialGroup);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<MaterialGroup> findMaterialGroupsWithConditions(MaterialGroupSearchCondition condition, Pageable pageable);
}
