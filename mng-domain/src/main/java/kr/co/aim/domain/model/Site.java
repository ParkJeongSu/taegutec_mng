package kr.co.aim.domain.model;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.handler.HasTransactionInfo;
import kr.co.aim.domain.command.SiteCreateCommand;
import kr.co.aim.domain.command.SiteUpdateCommand;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Site implements HasTransactionInfo {
    private Long id;
    private String siteName;
    private String description;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;

    public static Site create(SiteCreateCommand command) {
        Site site = Site.builder()
                .id(TsidUtils.nextId())
                .siteName(command.getSiteName())
                .description(command.getDescription())
                .build();

        if (command.getTransactionInfo() != null) {
            site.setEventName(command.getTransactionInfo().eventName());
            site.setEventTime(command.getTransactionInfo().eventTime());
            site.setEventUser(command.getTransactionInfo().eventUser());
            site.setEventComment(command.getTransactionInfo().eventComment());
        }
        return site;
    }

    public Site update(SiteUpdateCommand command) {
        if (command.getSiteName() != null) {
            setSiteName(command.getSiteName());
        }
        if (command.getDescription() != null) {
            setDescription(command.getDescription());
        }
        if (command.getTransactionInfo() != null) {
            setEventName(command.getTransactionInfo().eventName());
            setEventTime(command.getTransactionInfo().eventTime());
            setEventUser(command.getTransactionInfo().eventUser());
            setEventComment(command.getTransactionInfo().eventComment());
        }
        return this;
    }
}
