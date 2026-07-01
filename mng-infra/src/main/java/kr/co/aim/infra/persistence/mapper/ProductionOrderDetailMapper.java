package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.ProductionOrderDetail;
import kr.co.aim.infra.persistence.entity.ProductionOrderDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductionOrderDetailMapper {

    ProductionOrderDetail toDomain(ProductionOrderDetailEntity entity);

    ProductionOrderDetailEntity toEntity(ProductionOrderDetail domain);
}