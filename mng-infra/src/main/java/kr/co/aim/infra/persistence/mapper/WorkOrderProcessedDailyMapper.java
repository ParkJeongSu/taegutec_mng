package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WorkOrderProcessedDaily;
import kr.co.aim.infra.persistence.entity.WorkOrderProcessedDailyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class} // [핵심] 이 부분을 추가하세요!
)
public interface WorkOrderProcessedDailyMapper {

    WorkOrderProcessedDaily toDomain(WorkOrderProcessedDailyEntity entity);

    WorkOrderProcessedDailyEntity toEntity(WorkOrderProcessedDaily domain);

}