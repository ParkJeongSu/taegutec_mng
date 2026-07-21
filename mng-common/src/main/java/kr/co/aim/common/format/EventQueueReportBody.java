package kr.co.aim.common.format;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventQueueReportBody {
    private Long id;
    private String eventType;
    private String payload;
    private String ifStatus;
    private String carrierName;
    private String idocId;
    private String orderId;
    private String orderLineNumber;
    private Integer retryCNT;
    private String errMSG;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
