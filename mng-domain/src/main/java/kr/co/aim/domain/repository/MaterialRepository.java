package kr.co.aim.domain.repository;

import kr.co.aim.domain.model.Material;
import kr.co.aim.domain.repository.condition.MaterialSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaterialRepository {

    List<Material> findAll();

    Optional<Material> findById(Long id);

    Optional<Material> findByMaterialId(String materialId);

    Material save(Material material);

    void deleteById(Long id);

    void deleteAllByIdInBatch(List<Long> ids);

    Page<Material> findMaterialsWithConditions(MaterialSearchCondition condition, Pageable pageable);
}
