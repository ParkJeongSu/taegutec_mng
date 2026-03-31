package kr.co.aim.api.strategy;

import kr.co.aim.api.vo.insert.ops.InsertEventLogReportVo;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.command.InterfaceEventLogCreateCommand;

public interface FactoryProcessStrategy {
    public BaseMessage<TransportJobRequestListBody> carrierDispatchRequest(BaseMessage<CarrierDispatchRequestBody> message);
    public BaseMessage<DestinationDispatchRequestBody> unLoadRequest(BaseMessage<UnLoadRequestBody> message);
    public BaseMessage<TransportJobRequestListBody> transportOrderRequest(BaseMessage<TransportOrderRequestBody> message);
    public void loadCompleted(BaseMessage<LoadCompletedBody> message);
    public InterfaceEventLogCreateCommand createEventLogCommand(InsertEventLogReportVo vo);

}
