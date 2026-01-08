package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.AlarmActionUserGroupUsers;
import kr.co.aim.infra.persistence.entity.AlarmActionUserGroupUsersEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface AlarmActionUserGroupUsersMapper {

    AlarmActionUserGroupUsers toDomain(AlarmActionUserGroupUsersEntity entity);

    AlarmActionUserGroupUsersEntity toEntity(AlarmActionUserGroupUsers domain);
}