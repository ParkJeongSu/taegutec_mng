package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Equipment;
import kr.co.aim.infra.persistence.entity.EquipmentEntity;
import kr.co.aim.infra.persistence.entity.EquipmentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface EquipmentMapper {

    Equipment toDomain(EquipmentEntity entity);

    EquipmentEntity toEntity(Equipment domain);
    /**
     * Alarm 도메인 객체를 AlarmHistoryEntity로 변환합니다.
     * 'id' 필드는 데이터베이스에서 자동 생성되므로 매핑에서 제외합니다.
     */
    @Mapping(target = "id", ignore = true) // "id" 필드를 매핑 대상에서 제외
    EquipmentHistoryEntity toHistoryEntity(Equipment domain);
}