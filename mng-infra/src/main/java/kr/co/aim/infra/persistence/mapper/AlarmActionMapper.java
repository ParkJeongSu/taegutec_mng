package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.AlarmAction;
import kr.co.aim.infra.persistence.entity.AlarmActionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface AlarmActionMapper {

    AlarmAction toDomain(AlarmActionEntity entity);

    AlarmActionEntity toEntity(AlarmAction domain);
}