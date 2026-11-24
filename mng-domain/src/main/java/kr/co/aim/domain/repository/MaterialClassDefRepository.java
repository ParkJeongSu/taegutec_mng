package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.MaterialClassDef;
import kr.co.aim.domain.repository.condition.MaterialClassDefSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialClassDefRepository {

    List<MaterialClassDef> findAll();

    Optional<MaterialClassDef> findById(Long id);

    Optional<MaterialClassDef> findByMaterialClassDefName(String materialClassDefName);

    List<MaterialClassDef> findByParentClassId(Long parentClassId);

    MaterialClassDef save(MaterialClassDef materialClassDef);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<MaterialClassDef> findMaterialClassDefsWithConditions(MaterialClassDefSearchCondition condition, Pageable pageable);
}
