package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.TaskJob;
import kr.co.aim.infra.persistence.entity.TaskJobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaskJobMapper {

    TaskJob toDomain(TaskJobEntity entity);

    TaskJobEntity toEntity(TaskJob domain);
}