package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Equipments;
import kr.co.aim.infra.persistence.entity.EquipmentsEntity;
import kr.co.aim.infra.persistence.entity.EquipmentsHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentsMapper {

    Equipments toDomain(EquipmentsEntity entity);

    EquipmentsEntity toEntity(Equipments domain);
    /**
     * Alarm 도메인 객체를 AlarmHistoryEntity로 변환합니다.
     * 'id' 필드는 데이터베이스에서 자동 생성되므로 매핑에서 제외합니다.
     */
    @Mapping(target = "id", ignore = true) // "id" 필드를 매핑 대상에서 제외
    EquipmentsHistoryEntity toHistoryEntity(Equipments domain);
}