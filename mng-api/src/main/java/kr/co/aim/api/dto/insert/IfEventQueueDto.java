package kr.co.aim.api.dto.insert;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class IfEventQueueDto {

    private String messageName;
    private String eventType;
    private String transactionCode;
    private String carrierName;
    private String virtualCarrierName;
    private String idocId; // 기존 order idocId
    private String orderId;
    private String orderLineNumber; // insert 에선 현재로선 필요 없음
    private String orderType; // h2orderm.corderty
    private String galId;
    private String galWarehouse;
    private String requestedZoneName;
    private String errorText;
    private String actualWeight;
    private String actualZoneName;
    private String actualLocationId; // Rack Location or Location on Conveyor System.
    private String actualWorkStationId;



}