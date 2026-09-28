package kr.co.aim.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Schema(description = "최신 반송 오더 응답 DTO (TransportJob 정보 포함)")
public class RecentTransportOrderResponse {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "반송 오더 ID", example = "1001")
    private String transportOrderId;

    @Schema(description = "IDOC ID", example = "2001")
    private Long idocId;

    @Schema(description = "설명", example = "반송 작업 설명")
    private String description;

    @Schema(description = "캐리어 이름", example = "CARRIER_01")
    private String carrierName;

    @Schema(description = "가상 캐리어 이름", example = "VIRTUAL_01")
    private String virtualCarrierName;

    @Schema(description = "반송 구분 (I: Inbound, O: Outbound)", example = "I")
    private String transportType;

    @Schema(description = "반송 상태", example = "ACCEPTED")
    private String transportStatus;

    @Schema(description = "마지막 트랜잭션 코드", example = "TX_01")
    private String lastTransactionCode;

    @Schema(description = "캐리어 유형", example = "PALLET")
    private String carrierType;

    @Schema(description = "우선순위", example = "1")
    private Integer priority;

    @Schema(description = "GAL ID", example = "GAL_01")
    private String galId;

    @Schema(description = "GAL 창고", example = "WH_01")
    private String galWarehouse;

    @Schema(description = "위치 ID", example = "LOC_01")
    private String locationId;

    @Schema(description = "워크스테이션 ID", example = "341")
    private String workStationId;

    @Schema(description = "출발 존 이름", example = "ZONE_A")
    private String sourceZoneName;

    @Schema(description = "도착 존 이름", example = "ZONE_B")
    private String destinationZoneName;

    @Schema(description = "에러 메시지", example = "")
    private String errorText;

    @Schema(description = "실제 무게", example = "10.5")
    private String actualWeight;

    @Schema(description = "요청 존 이름", example = "ZONE_REQ")
    private String requestedZoneName;

    @Schema(description = "실제 존 이름", example = "ZONE_ACT")
    private String actualZoneName;

    @Schema(description = "실제 위치 ID", example = "LOC_ACT_01")
    private String actualLocationId;

    @Schema(description = "이동 프로파일", example = "DEFAULT")
    private String travelProfile;

    @Schema(description = "생성 일시")
    private LocalDateTime createTime;

    @Schema(description = "릴리즈 일시")
    private LocalDateTime releaseTime;

    @Schema(description = "완료 일시")
    private LocalDateTime completeTime;

    @Schema(description = "회수 일시")
    private LocalDateTime retrievalTime;

    @Schema(description = "생성자", example = "SYSTEM")
    private String createUser;

    @Schema(description = "릴리즈 사용자", example = "ADMIN")
    private String releaseUser;

    @Schema(description = "완료 사용자", example = "OPERATOR")
    private String completeUser;

    @Schema(description = "이벤트 이름", example = "ORDER_CREATE")
    private String eventName;

    @Schema(description = "이벤트 일시")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 사용자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "자동 생성")
    private String eventComment;

    // ✨ 추가 필드
    @Schema(description = "반송 잡 이름", example = "JOB_1001")
    private String transportJobName;

    @QueryProjection
    public RecentTransportOrderResponse(
            Long id, String transportOrderId, Long idocId, String description,
            String carrierName, String virtualCarrierName, String transportType,
            String transportStatus, String lastTransactionCode, String carrierType,
            Integer priority, String galId, String galWarehouse, String locationId,
            String workStationId, String sourceZoneName, String destinationZoneName,
            String errorText, String actualWeight, String requestedZoneName,
            String actualZoneName, String actualLocationId, String travelProfile,
            LocalDateTime createTime, LocalDateTime releaseTime, LocalDateTime completeTime,
            LocalDateTime retrievalTime, String createUser, String releaseUser,
            String completeUser, String eventName, LocalDateTime eventTime,
            String eventUser, String eventComment, String transportJobName
    ) {
        this.id = id;
        this.transportOrderId = transportOrderId;
        this.idocId = idocId;
        this.description = description;
        this.carrierName = carrierName;
        this.virtualCarrierName = virtualCarrierName;
        this.transportType = transportType;
        this.transportStatus = transportStatus;
        this.lastTransactionCode = lastTransactionCode;
        this.carrierType = carrierType;
        this.priority = priority;
        this.galId = galId;
        this.galWarehouse = galWarehouse;
        this.locationId = locationId;
        this.workStationId = workStationId;
        this.sourceZoneName = sourceZoneName;
        this.destinationZoneName = destinationZoneName;
        this.errorText = errorText;
        this.actualWeight = actualWeight;
        this.requestedZoneName = requestedZoneName;
        this.actualZoneName = actualZoneName;
        this.actualLocationId = actualLocationId;
        this.travelProfile = travelProfile;
        this.createTime = createTime;
        this.releaseTime = releaseTime;
        this.completeTime = completeTime;
        this.retrievalTime = retrievalTime;
        this.createUser = createUser;
        this.releaseUser = releaseUser;
        this.completeUser = completeUser;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventUser = eventUser;
        this.eventComment = eventComment;
        this.transportJobName = transportJobName;
    }
}
