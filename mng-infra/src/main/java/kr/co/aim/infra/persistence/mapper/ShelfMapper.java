package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Shelf;
import kr.co.aim.infra.persistence.entity.ShelfEntity;
import kr.co.aim.infra.persistence.entity.ShelfHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface ShelfMapper {

    Shelf toDomain(ShelfEntity entity);

    ShelfEntity toEntity(Shelf domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ShelfHistoryEntity toHistoryEntity(Shelf domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    ShelfHistoryEntity toHistoryEntity(ShelfEntity entity);
}
