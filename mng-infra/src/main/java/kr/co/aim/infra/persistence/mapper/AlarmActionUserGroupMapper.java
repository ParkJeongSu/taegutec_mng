package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.AlarmActionUserGroup;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface AlarmActionUserGroupMapper {

    AlarmActionUserGroup toDomain(AlarmActionUserGroupEntity entity);

    AlarmActionUserGroupEntity toEntity(AlarmActionUserGroup domain);
}