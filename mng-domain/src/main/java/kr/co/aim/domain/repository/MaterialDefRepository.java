package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.MaterialDef;
import kr.co.aim.domain.repository.condition.MaterialDefSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialDefRepository {

    List<MaterialDef> findAll();

    Optional<MaterialDef> findById(Long id);

    Optional<MaterialDef> findByMaterialDefName(String materialDefName);

    MaterialDef save(MaterialDef materialDef);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<MaterialDef> findMaterialDefsWithConditions(MaterialDefSearchCondition condition, Pageable pageable);
}
