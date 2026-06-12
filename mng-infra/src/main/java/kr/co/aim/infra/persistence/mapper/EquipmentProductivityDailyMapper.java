package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.EquipmentAvailabilityHourly;
import kr.co.aim.domain.model.EquipmentProductivityDaily;
import kr.co.aim.infra.persistence.entity.EquipmentAvailabilityHourlyEntity;
import kr.co.aim.infra.persistence.entity.EquipmentProductivityDailyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface EquipmentProductivityDailyMapper {

    EquipmentProductivityDaily toDomain(EquipmentProductivityDailyEntity entity);

    EquipmentProductivityDailyEntity toEntity(EquipmentProductivityDaily domain);

}