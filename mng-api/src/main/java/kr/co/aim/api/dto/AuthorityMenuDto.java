package kr.co.aim.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityMenuDto {
    private Long id;
    private String name;
    private List<AuthorityMenuItemDto> items;
}