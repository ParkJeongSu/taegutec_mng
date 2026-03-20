package kr.co.aim.api.dto.insert;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class StationOccupiedDto {
    private String containerId;
    private String locationId;
    private String workCenterId;
}