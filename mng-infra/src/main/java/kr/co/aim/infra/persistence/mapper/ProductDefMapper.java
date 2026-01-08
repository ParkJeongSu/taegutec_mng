package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.infra.persistence.entity.ProductDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductDefMapper {

    @Mapping(source = "XCount", target = "XCount") // 명시적으로 매핑
    @Mapping(source = "YCount", target = "YCount") // 명시적으로 매핑
    ProductDef toDomain(ProductDefEntity entity);

    @Mapping(source = "XCount", target = "xCount") // 명시적으로 매핑
    @Mapping(source = "YCount", target = "yCount") // 명시적으로 매핑
    ProductDefEntity toEntity(ProductDef domain);
}