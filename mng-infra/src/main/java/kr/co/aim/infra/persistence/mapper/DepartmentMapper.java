package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Department;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import kr.co.aim.infra.persistence.entity.DepartmentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface DepartmentMapper {

    Department toDomain(DepartmentEntity entity);

    DepartmentEntity toEntity(Department domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    DepartmentHistoryEntity toHistoryEntity(Department domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    DepartmentHistoryEntity toHistoryEntity(DepartmentEntity entity);
}
