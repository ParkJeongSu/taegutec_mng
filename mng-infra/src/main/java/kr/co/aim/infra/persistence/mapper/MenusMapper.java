package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Menus;
import kr.co.aim.infra.persistence.entity.MenusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenusMapper {

    Menus toDomain(MenusEntity entity);

    MenusEntity toEntity(Menus domain);
}