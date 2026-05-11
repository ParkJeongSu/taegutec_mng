package kr.co.aim.api.strategy;

public interface FactoryIfEventQueueStrategy {
    public void enqueueIfEventQueue(Object vo); // insert InsertEventQueueReportVo , powder : powderEventQueueReportVo
}
