package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.ProductDef;
import kr.co.aim.infra.persistence.entity.ProductDefEntity;
import kr.co.aim.infra.persistence.entity.ProductDefHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class } // [핵심] 이 부분을 추가하세요!
)
public interface ProductDefMapper {

    ProductDef toDomain(ProductDefEntity entity);

    ProductDefEntity toEntity(ProductDef domain);

    @Mapping(target = "id", expression = "java(TsidUtils.nextId())") // [3] 자바 코드 호출!
    ProductDefHistoryEntity toHistoryEntity(ProductDef domain);
}