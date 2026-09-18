package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Menu;
import kr.co.aim.infra.persistence.entity.MenuEntity;
import kr.co.aim.infra.persistence.entity.MenuHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class }
)
public interface MenuMapper {

    Menu toDomain(MenuEntity entity);

    MenuEntity toEntity(Menu domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())")
    MenuHistoryEntity toHistoryEntity(Menu domain);
}
