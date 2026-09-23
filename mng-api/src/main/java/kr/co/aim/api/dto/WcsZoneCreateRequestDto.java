package kr.co.aim.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsZoneCreateRequestDto {

    @NotBlank(message = "공장 구분은 필수 입력 항목입니다.")
    private String factoryName;

    @NotBlank(message = "존 명은 필수 입력 항목입니다.")
    private String zoneName;

    private Boolean deepFirstFlag;
    private Integer frontRowInterval;
    private String loadType;
    private BigDecimal maxCapacityPercent;
    private Integer zoneCapacity;
    private String zoneColor;
    private Integer zoneSize;
    private String zoneType;
    private String shelfSelectMode;
    private BigDecimal useCapacityPercent;
    private Boolean waitingAreaFlag;

    private String eventName;
    private String eventUser;
    private String eventComment;
}
