package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.MaterialDef;
import kr.co.aim.infra.persistence.entity.MaterialDefEntity;
import kr.co.aim.infra.persistence.entity.MaterialDefHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MaterialDefMapper {

    MaterialDef toDomain(MaterialDefEntity entity);

    MaterialDefEntity toEntity(MaterialDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialDefHistoryEntity toHistoryEntity(MaterialDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialDefHistoryEntity toHistoryEntity(MaterialDefEntity entity);
}
