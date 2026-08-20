package kr.co.aim.api.service;

import kr.co.aim.api.strategy.SchedulerProcessStrategy;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.command.*;
import kr.co.aim.domain.model.*;
import kr.co.aim.infra.persistence.entity.*;
import kr.co.aim.infra.persistence.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@Profile({"scheduler"})
public class MessageSchedulerExecuteService {

    private final SchedulerProcessStrategy schedulerProcessStrategy;

    public void eventQueueReport(BaseMessage<EventQueueReportBody> message) {
        schedulerProcessStrategy.eventQueueReport(message);
    }

}