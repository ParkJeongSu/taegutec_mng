package kr.co.aim.api.vo.insert.sim;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
@AllArgsConstructor
public class OrderCreateVo {
    private String orderId;           // 생성 orderId
    private String location;        // 넣는 위치
    private String locationGroup;   // 넣는 위치 그룹
    private String galId;           // GAL ID
    private String carrierId;       // Carrier ID
    private String carrierType;     // Carrier Type
    private String zoneName;        // 창고 Zone Name
    private String speed;           // 속도
    private String sourceZoneName;  // 출발지 Zone Name
    private String targetZoneName;  // 목적지 Zone Name
    private Integer orderPriority;     // order의 우선순위
}