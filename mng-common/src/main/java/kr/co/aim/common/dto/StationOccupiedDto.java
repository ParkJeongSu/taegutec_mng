package kr.co.aim.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class StationOccupiedDto {
    private String containerId;
    private String locationId;
    private String workcenterId;
}