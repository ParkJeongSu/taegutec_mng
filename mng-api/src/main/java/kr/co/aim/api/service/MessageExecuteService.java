package kr.co.aim.api.service;

import kr.co.aim.common.format.AlarmReportBody;
import kr.co.aim.common.format.request.BaseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class MessageExecuteService {
    /**
     * 알람은 log 만 찍음
     *
     *
     * @param message 받은 메시지
     */
    @Transactional // 이 메소드가 하나의 트랜잭션으로 동작하도록 보장합니다.
    public void alarmReport(BaseMessage<AlarmReportBody> message) {
        String alarmCode = message.getBody().getAlarmCode();
        String eventName = message.getMessageName();
        String eventUser = message.getMessageOwner();
        String eventComment =  message.getResultMessage();
        String equipmentName = message.getBody().getEquipmentName();
        String alarmState = message.getBody().getAlarmState();

        log.info("equipmentName : {}", equipmentName);
        log.info("alarmCode : {}", alarmCode);
        log.info("alarmState : {}", alarmState);
        log.info("alarmSeverity : {}", message.getBody().getAlarmSeverity());
        log.info("alarmText : {}", message.getBody().getAlarmText());

    }

}