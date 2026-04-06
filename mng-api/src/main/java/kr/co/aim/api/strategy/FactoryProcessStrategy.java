package kr.co.aim.api.strategy;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;

public interface FactoryProcessStrategy {
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message);
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message);
    public void loadCompleted(BaseMessage<LoadCompletedBody> message);
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) ;
}
