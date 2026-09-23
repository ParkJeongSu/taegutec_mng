package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsConveyor;
import kr.co.aim.infra.persistence.entity.WcsConveyorEntity;
import kr.co.aim.infra.persistence.entity.WcsConveyorHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsConveyorMapper {

    WcsConveyor toDomain(WcsConveyorEntity entity);

    WcsConveyorEntity toEntity(WcsConveyor domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsConveyorHistoryEntity toHistoryEntity(WcsConveyor domain);
}
