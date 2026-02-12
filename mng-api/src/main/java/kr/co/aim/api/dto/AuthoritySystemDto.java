package kr.co.aim.api.dto;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthoritySystemDto {
    private Long id;
    private String name;
    @Builder.Default
    private Map<String, AuthorityMenuDto> subMenus = new LinkedHashMap<>(); // 순서 보장을 위해 LinkedHashMap 사용
}