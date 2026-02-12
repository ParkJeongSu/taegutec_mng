package kr.co.aim.api.dto;

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