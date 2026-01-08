package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
public class PortDefSearchConditionDto {

    private Long id;
    private String portDefName;
    private String description;
    private String portType;
    private String portUseType;
    private Long useCarrierDefId;
}