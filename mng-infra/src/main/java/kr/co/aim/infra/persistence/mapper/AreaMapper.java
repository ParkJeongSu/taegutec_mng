package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Area;
import kr.co.aim.infra.persistence.entity.AreaEntity;
import kr.co.aim.infra.persistence.entity.AreaHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface AreaMapper {

    Area toDomain(AreaEntity entity);

    AreaEntity toEntity(Area domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    AreaHistoryEntity toHistoryEntity(Area domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    AreaHistoryEntity toHistoryEntity(AreaEntity entity);
}
