package kr.co.aim.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED) // JPA Entity 등을 위한 기본 생성자
@Builder
@AllArgsConstructor
public class TransportJobReplyDto {
    private Long id;
    private String transportJobName;
    private String carrierName;
    private String transportJobState;
    private String sourceEquipmentName;
    private String sourcePortName;
    private String sourceZoneName;
    private String sourceShelfName;
    private String destinationEquipmentName;
    private String destinationPortName;
    private String destinationZoneName;
    private String destinationShelfName;
    private Integer priority;
    private String errorCode;
    private String errorText;
    private String requestType;
    private LocalDateTime createTime;
    private String reasonCode;
    private String eventName;
    private LocalDateTime eventTime;
    private String eventUser;
    private String eventComment;
    private Long taskJobId;
}