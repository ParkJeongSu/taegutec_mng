package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WorkStation;
import kr.co.aim.infra.persistence.entity.WorkStationEntity;
import kr.co.aim.infra.persistence.entity.WorkStationHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface WorkStationMapper {

    WorkStation toDomain(WorkStationEntity entity);

    WorkStationEntity toEntity(WorkStation domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    WorkStationHistoryEntity toHistoryEntity(WorkStation domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    WorkStationHistoryEntity toHistoryEntity(WorkStationEntity entity);
}
