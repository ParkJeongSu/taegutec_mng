package kr.co.aim.common.handler;

import kr.co.aim.common.format.CarrierDispatchRequestBody;
import kr.co.aim.common.format.DestinationDispatchRequestBody;
import kr.co.aim.common.format.TransportJobRequestBody;
import kr.co.aim.common.format.UnLoadRequestBody;
import kr.co.aim.common.format.request.BaseMessage;

public interface DispatchStrategy {
    public BaseMessage<TransportJobRequestBody> requestDispatch(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);

}
