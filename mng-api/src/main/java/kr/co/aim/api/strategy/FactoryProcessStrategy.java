package kr.co.aim.api.strategy;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import org.springframework.context.annotation.Profile;

@Profile({"pex","tex","scheduler"})
public interface FactoryProcessStrategy {
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);
    public BaseMessage<TransportJobRequestListBody> transportOrderRequestList(BaseMessage<TransportOrderRequestBody> message);
    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message);
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message);
    public void loadCompleted(BaseMessage<LoadCompletedBody> message);
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) ;
}
