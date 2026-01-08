package kr.co.aim.infra.persistence.mapper;


import kr.co.aim.domain.model.AlarmMailActionDetail;
import kr.co.aim.infra.persistence.entity.AlarmMailActionDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
)
public interface AlarmMailActionDetailMapper {

    AlarmMailActionDetail toDomain(AlarmMailActionDetailEntity entity);

    AlarmMailActionDetailEntity toEntity(AlarmMailActionDetail domain);
}