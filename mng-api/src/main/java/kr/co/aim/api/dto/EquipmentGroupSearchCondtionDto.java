package kr.co.aim.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class EquipmentGroupSearchCondtionDto {

    private Long id;
    private String equipmentGroupName;
    private String description;
}