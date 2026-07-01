package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Lot;
import kr.co.aim.domain.model.LotHistory;
import kr.co.aim.infra.persistence.entity.LotEntity;
import kr.co.aim.infra.persistence.entity.LotHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface LotMapper {

    Lot toDomain(LotEntity entity);

    LotEntity toEntity(Lot domain);

    LotHistory toDomain(LotHistoryEntity entity);

    LotHistoryEntity toEntity(LotHistory domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    LotHistoryEntity toHistoryEntity(Lot domain);
}