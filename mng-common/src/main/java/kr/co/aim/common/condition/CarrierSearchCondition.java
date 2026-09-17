package kr.co.aim.common.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class CarrierSearchCondition {
    private String carrierName;
    private String carrierDefName;
    private String carrierState;
    private String equipmentName;
    private String portName;
    private String zoneName;
    private String positionTypeName;
    private String positionName;
    private Integer capacity;
    private String cleanState;
    private String transportState;
    private String transportJobId;
    private String holdState;
    private String reasonCode;
    private String useState;
    private Integer useCount;
    private Integer useCountPerClean;
    private Integer cleanCount;
    private BigDecimal quantity;
    private BigDecimal galQuantity;
    @Schema(description = "마지막 clean 일시", example = "2026-09-11T10:20:00", type = "string")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastCleanTime;
    @Schema(description = "생성 일시", example = "2026-09-11T10:20:00", type = "string")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createTime;
    @Schema(description = "창고 inbound 일시", example = "2026-09-11T10:20:00", type = "string")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime inboundTime;
    @Schema(description = "창고 outbound 일시", example = "2026-09-11T10:20:00", type = "string")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime outboundTime;
    private String containerType;

}