package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WorkOrder;
import kr.co.aim.infra.persistence.entity.WorkOrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkOrderMapper {

    WorkOrder toDomain(WorkOrderEntity entity);

    WorkOrderEntity toEntity(WorkOrder domain);
}