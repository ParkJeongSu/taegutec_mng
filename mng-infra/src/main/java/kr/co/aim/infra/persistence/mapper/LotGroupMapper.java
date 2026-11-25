package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.LotGroup;
import kr.co.aim.domain.model.LotGroupHistory;
import kr.co.aim.infra.persistence.entity.LotGroupEntity;
import kr.co.aim.infra.persistence.entity.LotGroupHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface LotGroupMapper {

    LotGroup toDomain(LotGroupEntity entity);

    LotGroupEntity toEntity(LotGroup domain);

    LotGroupHistory toDomain(LotGroupHistoryEntity entity);

    LotGroupHistoryEntity toEntity(LotGroupHistory domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    LotGroupHistoryEntity toHistoryEntity(LotGroup domain);
}