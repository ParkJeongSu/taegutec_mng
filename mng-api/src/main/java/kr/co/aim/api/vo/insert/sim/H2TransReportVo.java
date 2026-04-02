package kr.co.aim.api.vo.insert.sim;

import kr.co.aim.common.enums.GALTransportStatus;
import kr.co.aim.common.enums.IdocDataCode;
import kr.co.aim.common.enums.MessageList;
import kr.co.aim.common.enums.PortDetailType;
import kr.co.aim.domain.model.Port;
import kr.co.aim.domain.model.PortDef;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderDEntity;
import kr.co.aim.infra.persistence.db2entity.insert.H2OrderMEntity;
import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class H2TransReportVo {

    private final String transportJobName;
    private final String messageName;
    private final Port port;
    private final PortDef portDef;

    // 1. 상태 및 식별 정보 (핵심)
    private final GALTransportStatus status;  // 보고할 상태 (Enum)
    private final String orderId;
    private final String orderLineNumber;
    private final String carrierName;         // 캐리어/용기 ID

    // 2. 위치 및 경로 정보
    private final String requestZone;        // 구역 (CZONE)
    private final String actualZone;
    private final String locationCode;       // 구체적 위치 코드

    // 20 : INBOUND , 40 : OUTBOUND,RELOCATION
    private final IdocDataCode idocDataCode;

    // 측정 무게
    private final Long weight;

    // 3. 예외 처리 정보
    private final String errorText;

    // 4. 원천 데이터 엔티티 (DB 저장용 참조)
    private final IdocEntity sourceIdoc;     // 기준이 된 IDOC
    private final H2OrderMEntity master;     // 주문 마스터
    private final List<H2OrderDEntity> details;        // DB2 Details
    private final IdocEntity newIdoc;        // new IDOC

    // 리스트 중 첫 번째 디테일을 반환 (단건 처리용)
    public H2OrderDEntity getFirstDetail() {
        if (details == null || details.isEmpty()) return null;
        return details.get(0);
    }

    private static GALTransportStatus deriveStatus(String messageName, Port port, PortDef portDef) {
        if (StringUtils.equals(MessageList.LOAD_COMPLETE.getMessageName(), messageName)) {
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Station Occupied case
                // 106 report
                // 반송잡이 없음
                return GALTransportStatus.StationOccupied;
            } else if (StringUtils.equals(PortDetailType.WORKSTATION.getValue(), portDef.getDetailPortType())) {
                // 반송잡이 있으면 해당 반송잡으로 아래보고
                // outbound case
                // 108 Outbound Arrival At workStation report
                // 90 outbound order Done report
                // 반송잡이 없다면,
                // 가장 최신 변경된 transportOrder 으로 108,90 보고
            }
        } else if (StringUtils.equals(MessageList.UNLOAD_COMPLETE.getMessageName(), messageName)) {
            if (StringUtils.equals(PortDetailType.INBOUND.getValue(), portDef.getDetailPortType())) {
                // Inbound Workstation empty
                // 105 repot
            }
        } else if (StringUtils.equals(MessageList.CARRIER_SCANNED.getMessageName(), messageName)) {
            // Inbound ContainerId is Scanned
            // 126 repot
        } else if (StringUtils.equals(MessageList.CARRIER_LOCATION_CHANGED.getMessageName(), messageName)) {
            // 이 경우는 TransportOrder가 있을수도 없을수도 있음
            // orderId가 있을수도 없을 수도 있다는 이야기
            if (StringUtils.equals(PortDetailType.OUT_OF_RACK.getValue(), portDef.getDetailPortType())) {
                // Out of Rack
                // 109 repot
            } else if (StringUtils.equals(PortDetailType.TUNNEL.getValue(), portDef.getDetailPortType())) {
                // S/R Machine dropped container on tunnel conveyor
                // 109 report
            }

        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_COMPLETED.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재

            // Type : Inbound Case
            // 107 Arrival at Rack report
            // 92 Inbound order Done report

            // Type : Outbound Case
            // 109 Out of Rack report

            // Type : Relocation Case
            // #1 orderId 가 존재하면
            // 107 Arrival at Rack report
            // 94 Relocation order confirmation report

            // #2 orderId 가 존재하지 않는다면
            // 114 internal Relocation report
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_REPLY.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 2 Accept report
        } else if (StringUtils.equals(MessageList.TRANSPORT_JOB_STARTED.getMessageName(), messageName)) {
            // 무조건 TransportJob 은 존재
            // Type : Inbound Case
            // Type : Outbound Case
            // Type : Relocation Case
            // 2 Accept report
        }

        return null;
    }


}
