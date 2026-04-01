package kr.co.aim.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class InterfaceEventLogDto {

    private String messageName;
    private String eventType;
    private String transactionCode;
    private String carrierName;
    private String idocId;
    private String orderId;
    private String orderLineNumber; // insert 에선 현재로선 필요 없음
    private String orderType; // inbound,outbound,relocation
    private String errorText;
    private String actualWeight;
    private String actualZoneName;
    private String actualRackLocationId;

}