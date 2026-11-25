package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.MaterialLot;
import kr.co.aim.infra.persistence.entity.MaterialLotEntity;
import kr.co.aim.infra.persistence.entity.MaterialLotHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MaterialLotMapper {

    MaterialLot toDomain(MaterialLotEntity entity);

    MaterialLotEntity toEntity(MaterialLot domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialLotHistoryEntity toHistoryEntity(MaterialLot domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MaterialLotHistoryEntity toHistoryEntity(MaterialLotEntity entity);
}
