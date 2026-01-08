package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Equipments;
import kr.co.aim.domain.model.Ports;
import kr.co.aim.infra.persistence.entity.EquipmentsEntity;
import kr.co.aim.infra.persistence.entity.PortsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PortsMapper {

    //@BeanMapping(ignoreByDefault = true) // 👈 이 한 줄 추가
    //BeanMapping 이라는건 기본적으로 모든 필드를 무시하고 @Mapping 으로 명시한 컬럼만 매핑
    Ports toDomain(PortsEntity entity);

    PortsEntity toEntity(Ports domain);
}