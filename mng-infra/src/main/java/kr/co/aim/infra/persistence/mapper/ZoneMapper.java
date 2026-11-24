package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Zone;
import kr.co.aim.infra.persistence.entity.ZoneEntity;
import kr.co.aim.infra.persistence.entity.ZoneHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface ZoneMapper {

    Zone toDomain(ZoneEntity entity);

    ZoneEntity toEntity(Zone domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneHistoryEntity toHistoryEntity(Zone domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ZoneHistoryEntity toHistoryEntity(ZoneEntity entity);
}
