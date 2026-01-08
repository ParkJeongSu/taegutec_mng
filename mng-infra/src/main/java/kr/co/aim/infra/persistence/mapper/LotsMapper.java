package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Lots;
import kr.co.aim.infra.persistence.entity.LotsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LotsMapper {

    Lots toDomain(LotsEntity entity);

    LotsEntity toEntity(Lots domain);
}