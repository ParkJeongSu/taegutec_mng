package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.CarrierDef;
import kr.co.aim.infra.persistence.entity.CarrierDefEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = { kr.co.aim.common.Utils.TsidUtils.class } // [핵심] 이 부분을 추가하세요!
)
public interface CarrierDefMapper {

    CarrierDef toDomain(CarrierDefEntity entity);

    CarrierDefEntity toEntity(CarrierDef domain);
}