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
    private String transactionCode;
    private String carrierName;
    private String idocId;
    private String orderId;
    private String orderLineNumber;
    private String errorText;
    private String actualWeight;
    private String actualZoneName;
    private String actualRackLocationId;

}