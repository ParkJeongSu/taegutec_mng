package kr.co.aim.domain.repository.condition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteSearchCondition {
    private String siteName;
    private String description;
    private String eventName;
    private String eventUser;
}
