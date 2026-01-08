package kr.co.aim.common.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityMenuChildItemDto {
    private Long id;
    private String title;
    private String icon;
    private String to;

}