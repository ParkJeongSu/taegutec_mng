package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsAlarm;
import kr.co.aim.infra.persistence.entity.WcsAlarmEntity;
import kr.co.aim.infra.persistence.entity.WcsAlarmHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsAlarmMapper {

    WcsAlarm toDomain(WcsAlarmEntity entity);

    WcsAlarmEntity toEntity(WcsAlarm domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsAlarmHistoryEntity toHistoryEntity(WcsAlarm domain);
}
