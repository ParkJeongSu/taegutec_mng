package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.AlarmDef;
import kr.co.aim.infra.persistence.entity.AlarmDefEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface AlarmDefMapper {

    AlarmDef toDomain(AlarmDefEntity entity);

    AlarmDefEntity toEntity(AlarmDef domain);
}