package kr.co.aim.api.vo.carrier;

import kr.co.aim.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CarrierSelectionResult {
    private final Carrier carrier;
    private final String orderId;
    private final String orderLineNumber;
}