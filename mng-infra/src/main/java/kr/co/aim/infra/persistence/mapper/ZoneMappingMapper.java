package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.ZoneMapping;
import kr.co.aim.infra.persistence.entity.ZoneMappingEntity;
import kr.co.aim.infra.persistence.entity.ZoneMappingHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface ZoneMappingMapper {

    ZoneMapping toDomain(ZoneMappingEntity entity);

    ZoneMappingEntity toEntity(ZoneMapping domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneMappingHistoryEntity toHistoryEntity(ZoneMapping domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneMappingHistoryEntity toHistoryEntity(ZoneMappingEntity entity);
}
