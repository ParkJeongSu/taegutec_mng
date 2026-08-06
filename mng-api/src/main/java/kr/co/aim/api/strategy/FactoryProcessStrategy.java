package kr.co.aim.api.strategy;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import org.springframework.context.annotation.Profile;

@Profile({"pex","tex","scheduler"})
public interface FactoryProcessStrategy {
    public BaseMessage<TransportJobRequestBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);
    public BaseMessage<TransportJobRequestBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message);
    public void unLoadCompleted(BaseMessage<UnLoadCompletedBody> message);
    public BaseMessage<CarrierInfoDownloadSendBody> loadCompleted(BaseMessage<LoadCompletedBody> message);
    public void carrierLocationChanged(BaseMessage<CarrierLocationChangedBody> message) ;
    public void transportJobCancelCompleted(BaseMessage<TransportJobCancelCompletedBody> message) ;
    public void transportJobCompleted(BaseMessage<TransportJobCompletedBody> message);
    public void transportJobReply(BaseMessage<TransportJobReplyBody> message) ;
    public void transportJobStarted(BaseMessage<TransportJobStartedBody> message) ;
    public BaseMessage<CarrierDispatchRequestBody> loadRequest(BaseMessage<LoadRequestBody> message) ;
    public BaseMessage<TransportJobValidationRequestBody> transportOrderValidationRequest(BaseMessage<TransportOrderRequestBody> message);
    public void transportJobValidationReply(BaseMessage<TransportJobValidationReplyBody> message) ;
    public void eventQueueReport(BaseMessage<EventQueueReportBody> message) ;
    public void productionOrderAllocateRequest(BaseMessage<ProductionOrderAllocateRequestBody> message);
}
