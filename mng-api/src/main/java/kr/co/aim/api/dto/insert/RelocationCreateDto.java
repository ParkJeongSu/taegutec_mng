package kr.co.aim.api.dto.insert;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelocationCreateDto {
    private String carrierId;       // 옮기고자 하는 Carrier ID
    private String carrierType;     // Carrier Type
    private String sourceZoneName;  // 출발지 Zone Name
    private String targetZoneName;  // 목적지 Zone Name
    private String galId;           // GAL ID
}