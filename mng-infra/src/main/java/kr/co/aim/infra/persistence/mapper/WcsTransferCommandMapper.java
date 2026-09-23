package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsTransferCommand;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsTransferCommandHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsTransferCommandMapper {

    WcsTransferCommand toDomain(WcsTransferCommandEntity entity);

    WcsTransferCommandEntity toEntity(WcsTransferCommand domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsTransferCommandHistoryEntity toHistoryEntity(WcsTransferCommand domain);
}
