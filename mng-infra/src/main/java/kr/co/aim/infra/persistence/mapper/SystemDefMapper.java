package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.SystemDef;
import kr.co.aim.infra.persistence.entity.SystemDefEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SystemDefMapper {

    SystemDef toDomain(SystemDefEntity entity);

    SystemDefEntity toEntity(SystemDef domain);
}