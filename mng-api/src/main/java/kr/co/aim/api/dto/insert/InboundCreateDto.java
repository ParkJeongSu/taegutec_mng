package kr.co.aim.api.dto.insert;

import lombok.*;

@Getter
@Setter
@Builder
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