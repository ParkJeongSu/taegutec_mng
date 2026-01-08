package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.AuthorityMenus;
import kr.co.aim.infra.persistence.entity.AuthorityMenusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityMenusMapper {

    AuthorityMenus toDomain(AuthorityMenusEntity entity);

    AuthorityMenusEntity toEntity(AuthorityMenus domain);
}