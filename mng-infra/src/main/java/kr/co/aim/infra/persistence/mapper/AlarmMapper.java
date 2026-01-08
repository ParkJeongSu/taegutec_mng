package kr.co.aim.infra.persistence.mapper;

import kr.co.aim.domain.model.Alarm;
import kr.co.aim.infra.persistence.entity.AlarmEntity;
import kr.co.aim.infra.persistence.entity.AlarmHistoryEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import kr.co.aim.common.Utils.TsidUtils; // [1] 사용할 유틸 클래스 import

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        // builder = @Builder(disableBuilder = true)  <- 이 라인을 삭제
        imports = { TsidUtils.class } // [2] 여기에 클래스를 등록해야 expression에서 씁니다.
)
public interface AlarmMapper {

    Alarm toDomain(AlarmEntity entity);

    AlarmEntity toEntity(Alarm domain);
    /**
     * Alarm 도메인 객체를 AlarmHistoryEntity로 변환합니다.
     * 'id' 필드는 데이터베이스에서 자동 생성되므로 매핑에서 제외합니다.
     */
    @Mapping(target = "id", expression = "java(TsidUtils.nextId())") // [3] 자바 코드 호출!
    AlarmHistoryEntity toHistoryEntity(Alarm domain);
}