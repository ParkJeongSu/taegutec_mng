package kr.co.aim.api.vo.insert;

import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class H2TransEntityVo {
    // 1. 상태 및 식별 정보 (핵심)
    private final GALTransportStatus status;  // 보고할 상태 (Enum)
    private final String containerId;         // 캐리어/용기 ID

    // 2. 위치 및 경로 정보
    private final String zone;               // 구역 (CZONE)
    private final String locationCode;       // 구체적 위치 코드

    // 3. 예외 처리 정보
    private final String errorText;          // 에러 발생 시 내용

    // 4. 원천 데이터 엔티티 (DB 저장용 참조)
    private final IdocEntity newIdoc;     // 기준이 된 IDOC
    private final H2OrderMEntity master;     // 주문 마스터
    private final List<H2OrderDEntity> details;        // DB2 Details

    // 리스트 중 첫 번째 디테일을 반환 (단건 처리용)
    public H2OrderDEntity getFirstDetail() {
        if (details == null || details.isEmpty()) return null;
        return details.get(0);
    }
}
