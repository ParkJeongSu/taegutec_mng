package kr.co.aim.api.strategy;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import org.springframework.context.annotation.Profile;

@Profile({"scheduler"})
public interface SchedulerProcessStrategy {
    public void eventQueueReport(BaseMessage<EventQueueReportBody> message) ;
}
