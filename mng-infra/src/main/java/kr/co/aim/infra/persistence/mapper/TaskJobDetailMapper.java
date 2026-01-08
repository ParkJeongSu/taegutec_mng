package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.TaskJobDetail;
import kr.co.aim.infra.persistence.entity.TaskJobDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TaskJobDetailMapper {

    TaskJobDetail toDomain(TaskJobDetailEntity entity);

    TaskJobDetailEntity toEntity(TaskJobDetail domain);
}