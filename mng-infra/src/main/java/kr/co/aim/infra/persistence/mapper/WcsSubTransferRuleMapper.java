package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsSubTransferRule;
import kr.co.aim.infra.persistence.entity.WcsSubTransferRuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsSubTransferRuleMapper {

    WcsSubTransferRule toDomain(WcsSubTransferRuleEntity entity);

    WcsSubTransferRuleEntity toEntity(WcsSubTransferRule domain);
}
