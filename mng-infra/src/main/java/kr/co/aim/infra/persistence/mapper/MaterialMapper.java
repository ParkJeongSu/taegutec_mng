package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Material;
import kr.co.aim.infra.persistence.entity.MaterialEntity;
import kr.co.aim.infra.persistence.entity.MaterialHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MaterialMapper {

    Material toDomain(MaterialEntity entity);

    MaterialEntity toEntity(Material domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialHistoryEntity toHistoryEntity(Material domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialHistoryEntity toHistoryEntity(MaterialEntity entity);
}
