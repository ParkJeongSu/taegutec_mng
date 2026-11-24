package kr.co.aim.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.aim.domain.model.Shelf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "선반(SHELF) 응답 DTO")
public class ShelfResponse {

    @Schema(description = "고유 ID (TSID)", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "위치 ID", example = "LOC-001")
    private String locationId;

    @Schema(description = "설비명", example = "STK-01")
    private String equipmentName;

    @Schema(description = "존 ID", example = "877810665130787535")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long zoneId;

    @Schema(description = "구역 ID", example = "877810665130787536")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long areaId;

    @Schema(description = "적재 용량", example = "1")
    private Integer capacity;

    @Schema(description = "선반 상태", example = "EMPTY")
    private String shelfState;

    @Schema(description = "통신 상태", example = "ONLINE")
    private String communicationState;

    @Schema(description = "홀드 상태", example = "NONE")
    private String holdState;

    @Schema(description = "길이", example = "100")
    private Integer length;

    @Schema(description = "너비", example = "100")
    private Integer width;

    @Schema(description = "높이", example = "100")
    private Integer height;

    @Schema(description = "이벤트 이름", example = "ShelfCreated")
    private String eventName;

    @Schema(description = "이벤트 시간", example = "2026-11-24T16:00:00")
    private LocalDateTime eventTime;

    @Schema(description = "이벤트 작업자", example = "SYSTEM")
    private String eventUser;

    @Schema(description = "이벤트 코멘트", example = "Shelf created")
    private String eventComment;

    public static ShelfResponse fromDomain(Shelf domain) {
        if (domain == null) {
            return null;
        }
        return ShelfResponse.builder()
                .id(domain.getId())
                .locationId(domain.getLocationId())
                .equipmentName(domain.getEquipmentName())
                .zoneId(domain.getZoneId())
                .areaId(domain.getAreaId())
                .capacity(domain.getCapacity())
                .shelfState(domain.getShelfState())
                .communicationState(domain.getCommunicationState())
                .holdState(domain.getHoldState())
                .length(domain.getLength())
                .width(domain.getWidth())
                .height(domain.getHeight())
                .eventName(domain.getEventName())
                .eventTime(domain.getEventTime())
                .eventUser(domain.getEventUser())
                .eventComment(domain.getEventComment())
                .build();
    }
}
