package kr.co.aim.common.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityMenuItemDto {
    private Long id;
    private String title;
    private String icon;
    private String to;
    private List<AuthorityMenuChildItemDto> children;
}