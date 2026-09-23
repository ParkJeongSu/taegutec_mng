package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsCrane;
import kr.co.aim.infra.persistence.entity.WcsCraneEntity;
import kr.co.aim.infra.persistence.entity.WcsCraneHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsCraneMapper {

    WcsCrane toDomain(WcsCraneEntity entity);

    WcsCraneEntity toEntity(WcsCrane domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsCraneHistoryEntity toHistoryEntity(WcsCrane domain);
}
