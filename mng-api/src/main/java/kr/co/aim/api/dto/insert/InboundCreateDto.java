package kr.co.aim.api.dto.insert;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InboundCreateDto {
    private String location;        // 넣는 위치
    private String locationGroup;   // 넣는 위치 그룹
    private String galId;           // GAL ID
    private String carrierId;       // Carrier ID
    private String carrierType;     // Carrier Type
    private String zoneName;        // 창고 Zone Name
    private String speed;           // 속도
}