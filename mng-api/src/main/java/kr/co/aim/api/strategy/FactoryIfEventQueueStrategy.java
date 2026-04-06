package kr.co.aim.api.strategy;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;

public interface FactoryIfEventQueueStrategy {
    public void enqueueIfEventQueue(Object vo); // insert InsertEventQueueReportVo , powder : powderEventQueueReportVo
}
