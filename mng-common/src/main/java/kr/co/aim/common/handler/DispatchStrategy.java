package kr.co.aim.common.handler;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;

public interface DispatchStrategy {
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message);

}
