package kr.co.aim.api.service;

import kr.co.aim.common.format.ProcessJobAbortedBody;
import kr.co.aim.common.format.ProcessJobDataReportBody;
import kr.co.aim.common.format.ProcessJobEndedBody;
import kr.co.aim.common.format.ProcessJobStartedBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.domain.repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class ProcessJobService {

    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;


}