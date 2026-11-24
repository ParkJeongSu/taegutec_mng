package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.MaterialGroup;
import kr.co.aim.infra.persistence.entity.MaterialGroupEntity;
import kr.co.aim.infra.persistence.entity.MaterialGroupHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MaterialGroupMapper {

    MaterialGroup toDomain(MaterialGroupEntity entity);

    MaterialGroupEntity toEntity(MaterialGroup domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialGroupHistoryEntity toHistoryEntity(MaterialGroup domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialGroupHistoryEntity toHistoryEntity(MaterialGroupEntity entity);
}
