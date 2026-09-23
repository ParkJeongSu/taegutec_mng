package kr.co.aim.api.dto;

import kr.co.aim.domain.model.WcsShelf;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WcsShelfResponse {

    private String factoryName;
    private String shelfName;
    private String stockerName;

    private Integer abnormalStageNumber;
    private Integer bin;
    private String carrierName;
    private Integer col;
    private String lastTransferCmdName;
    private Integer numberOfUses;
    private Integer row;
    private String shelfEnableMode;
    private String shelfStatus;
    private String shelfTransferStatus;
    private String shelfType;
    private Integer stage;
    private String zoneName;

    private String lastEventComment;
    private String lastEventName;
    private LocalDateTime lastEventTime;
    private String lastEventUser;

    public static WcsShelfResponse fromDomain(WcsShelf domain) {
        if (domain == null) {
            return null;
        }

        return WcsShelfResponse.builder()
                .factoryName(domain.getFactoryName())
                .shelfName(domain.getShelfName())
                .stockerName(domain.getStockerName())
                .abnormalStageNumber(domain.getAbnormalStageNumber())
                .bin(domain.getBin())
                .carrierName(domain.getCarrierName())
                .col(domain.getCol())
                .lastTransferCmdName(domain.getLastTransferCmdName())
                .numberOfUses(domain.getNumberOfUses())
                .row(domain.getRow())
                .shelfEnableMode(domain.getShelfEnableMode())
                .shelfStatus(domain.getShelfStatus())
                .shelfTransferStatus(domain.getShelfTransferStatus())
                .shelfType(domain.getShelfType())
                .stage(domain.getStage())
                .zoneName(domain.getZoneName())
                .lastEventComment(domain.getLastEventComment())
                .lastEventName(domain.getLastEventName())
                .lastEventTime(domain.getLastEventTime())
                .lastEventUser(domain.getLastEventUser())
                .build();
    }
}
