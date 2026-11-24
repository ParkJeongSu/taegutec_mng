package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Factory;
import kr.co.aim.infra.persistence.entity.FactoryEntity;
import kr.co.aim.infra.persistence.entity.FactoryHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface FactoryMapper {

    Factory toDomain(FactoryEntity entity);

    FactoryEntity toEntity(Factory domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    FactoryHistoryEntity toHistoryEntity(Factory domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    FactoryHistoryEntity toHistoryEntity(FactoryEntity entity);
}
