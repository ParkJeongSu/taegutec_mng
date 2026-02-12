package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.common.dto.TransportOrderResponseDto;
import kr.co.aim.infra.persistence.entity.TransportOrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface TransportOrderMapper {

    TransportOrderResponseDto toDto(TransportOrderEntity entity);
}