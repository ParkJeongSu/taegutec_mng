package kr.co.aim.common.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AuthoritySearchConditionDto {
    private Long id;
    private String authorityName;
}
