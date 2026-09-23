package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.WcsSubTransferCommand;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WcsSubTransferCommandMapper {

    WcsSubTransferCommand toDomain(WcsSubTransferCommandEntity entity);

    WcsSubTransferCommandEntity toEntity(WcsSubTransferCommand domain);

    @Mapping(target = "eventTimeKey", expression = "java(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern(\"yyyyMMddHHmmssSSS\")))")
    @Mapping(target = "eventName", source = "lastEventName")
    @Mapping(target = "eventTime", source = "lastEventTime")
    @Mapping(target = "eventUser", source = "lastEventUser")
    @Mapping(target = "eventComment", source = "lastEventComment")
    WcsSubTransferCommandHistoryEntity toHistoryEntity(WcsSubTransferCommand domain);
}
