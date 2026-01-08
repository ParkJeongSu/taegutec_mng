package kr.co.aim.api.service;

import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.CleanJobStartedCommand;
import kr.co.aim.domain.command.ProcessJobEndedCommand;
import kr.co.aim.domain.command.ProcessJobStartedCommand;
import kr.co.aim.domain.model.Carriers;
import kr.co.aim.domain.model.Lots;
import kr.co.aim.domain.repository.*;
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
    private final CarriersRepository carriersRepository;
    private final LotsRepository lotsRepository;

    /**
     * 설비에 투입 후 생산 중단 보고
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void processJobAborted(BaseMessage<ProcessJobAbortedBody> message) {
        // TODO: Container 의 원자재를 설비에 넣다가 중단되고 설비의 투입된 Lot과 split 됨
        // 이 경우 어떤식으로 보고가 오는지 확인...
        // Container 에 있는게 신규 Lot 으로 만들어져야 할 것 같고
        // 이때 어떤 naming rule 에 의해서 만들어질건지
        // 그리고 wms나 gal 에는 보고를 할것같은데 어떤식으로 보고를 할건지.
    }

    /**
     * 설비에 투입 후 보고
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void processJobStarted(BaseMessage<ProcessJobStartedBody> message) {

        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String lotName = message.getBody().getLotName();
        String carrierName = message.getBody().getCarrierName();
        String recipeName = message.getBody().getRecipeName();

        Optional<Lots> optionalLots = lotsRepository.findByLotName(lotName);
        if(optionalLots.isEmpty()){
            // TODO: try catch 구조로 해서 wms에 뭔가를 보고해야하는지 확인..
            return;
        }
        Lots lot = optionalLots.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        ProcessJobStartedCommand command = ProcessJobStartedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .recipeName(recipeName)
                .build();

        lot.processJobStarted(command);
        lotsRepository.save(lot);
        
        // TODO: ProcessJobStarted 보고 후 wms에 보고 해야한다면 어떤식으로 할지 논의
    }


    /**
     * 설비에 투입 후 완료 보고
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void processJobEnded(BaseMessage<ProcessJobEndedBody> message) {
        // TODO : 작업종료와 Carrier Assign 을 분리하는지 이때 recipe 보고 하는지..
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        

        String equipmentName = message.getBody().getEquipmentName();
        String portName = message.getBody().getPortName();
        String lotName = message.getBody().getLotName();

        Optional<Lots> optionalLots = lotsRepository.findByLotName(lotName);
        if(optionalLots.isEmpty()){
            // TODO: try catch 구조로 해서 wms에 뭔가를 보고해야하는지 확인..
            return;
        }
        Lots lot = optionalLots.get();

        TransactionInfo tx = TransactionInfo.now(eventName,eventUser,eventComment);
        ProcessJobEndedCommand command = ProcessJobEndedCommand.builder()
                .transactionInfo(tx)
                .equipmentName(equipmentName)
                .recipeName("")
                .build();
        lot.processJobEnded(command);
        lotsRepository.save(lot);

        // TODO: ProcessJobStarted 보고 후 wms에 보고 해야한다면 어떤식으로 할지 논의
    }

    /**
     * 설비에 투입 후 완료 후 데이터 보고
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void processJobDataReport(BaseMessage<ProcessJobDataReportBody> message) {
        // TODO: 이걸 sv 데이터라고 하나...? 어째든 기록만 한다고 하는데.. 어떻게 테이블 구조 될지 문의
    }
}