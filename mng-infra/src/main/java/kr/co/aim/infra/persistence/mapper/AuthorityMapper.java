package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Authority;
import kr.co.aim.infra.persistence.entity.AuthorityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    Authority toDomain(AuthorityEntity entity);

    AuthorityEntity toEntity(Authority domain);
}