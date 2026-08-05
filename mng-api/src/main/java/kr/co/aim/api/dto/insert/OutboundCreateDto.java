package kr.co.aim.api.dto.insert;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundCreateDto {
    private String locationGroup;   // 꺼내고자 하는 위치 그룹
    private String galId;           // GAL ID
    private String carrierId;       // Carrier ID
    private String carrierType;     // Carrier Type
    private String zoneName;        // 창고 Zone Name
    private Integer orderPriority;     // order의 우선순위
}