package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.MaterialClassDef;
import kr.co.aim.infra.persistence.entity.MaterialClassDefEntity;
import kr.co.aim.infra.persistence.entity.MaterialClassDefHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MaterialClassDefMapper {

    MaterialClassDef toDomain(MaterialClassDefEntity entity);

    MaterialClassDefEntity toEntity(MaterialClassDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialClassDefHistoryEntity toHistoryEntity(MaterialClassDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialClassDefHistoryEntity toHistoryEntity(MaterialClassDefEntity entity);
}
