package kr.co.aim.api.vo.insert;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
@AllArgsConstructor
public class StationOccupiedVo {
    private String carrierName;
    private String locationId;
    private String workCenterId;
}