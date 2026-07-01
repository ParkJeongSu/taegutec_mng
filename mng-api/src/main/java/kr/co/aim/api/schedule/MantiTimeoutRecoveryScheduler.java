package kr.co.aim.api.schedule;

import kr.co.aim.api.service.CarrierService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.CarrierInfoDownloadSendBody;
import kr.co.aim.common.format.RecipeBody;
import kr.co.aim.common.format.RecipeParameterListBody;
import kr.co.aim.common.format.request.BaseMessage;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.LotCarrierMapping;
import kr.co.aim.infra.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class MantiTimeoutRecoveryScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final CarrierService carrierService;

    /**
     * 2분마다 실행 (120,000ms)
     * MANTI 레시피 서버로 요청을 보낸 뒤 2분이 지나도록 Reply가 없는 캐리어를 구출하거나 이력을 재발행합니다.
     */
    @Scheduled(fixedDelay = 120000)
    @SchedulerLock(name = "mantiTimeoutProcess",
            lockAtMostFor = "PT1M45S",   // 스케줄 간격(2분)보다 살짝 작게 잡아 락 겹침 방지
            lockAtLeastFor = "PT10S")
    public void checkMantiTimeout() {
        log.info("### MANTI Timeout Detection Scheduler Started ###");

        // 현재 시간 기준 2분 전 시점 계산 (Threshold)
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(2);

        // WAIT 상태이면서 요청한 지 2분이 지났고, REPLY 시간은 비어있는 타겟 추출
        List<LotCarrierMapping> timeoutList = lotCarrierMappingService.findMantiTimeoutTargets(
                MantiRequestState.WAIT.getValue(),
                thresholdTime
        );

        if (CollectionUtils.isNotEmpty(timeoutList)) {
            log.warn("[MANTI TIMEOUT] 무응답 타겟 {}건 발견.", timeoutList.size());

            for (LotCarrierMapping mapping : timeoutList) {
                LocalDateTime currentDateTime = LocalDateTime.now();
                log.warn("-> 타임아웃 디테일 - Carrier: {}, Lot: {}, MNG_KEY: {}, RequestTime: {}",
                        mapping.getCarrierName(), mapping.getLotName(), mapping.getMngKey(), mapping.getMantiRequestTime());

                Optional<Carrier> optionalCarrier = carrierService.findByCarrierName(mapping.getCarrierName());
                if (optionalCarrier.isEmpty()) {
                    continue;
                }
                Carrier carrier = optionalCarrier.get();

                // 재전송 큐 메시지 생성 (전통적인 빈 생성 및 setter 기법 활용)
                BaseMessage<CarrierInfoDownloadSendBody> request = new BaseMessage<>();

                request.setMessageName(MessageList.CARRIER_INFO_DOWNLOAD_SEND.getMessageName());
                request.setTransactionId(FormatUtils.getTransactionId(currentDateTime));
                request.setMessageFrom(SystemName.MNG.getValue());
                request.setMessageOwner(SystemName.MNG.getValue());
                request.setMessageTo(SystemName.EAS.getValue());
                request.setEventTime(FormatUtils.getTransactionId(currentDateTime));
                request.setResultMessage("");
                request.setResultCode(ResultCode.NG.getValue());

                RecipeBody recipeBody = new RecipeBody();
                List<RecipeParameterListBody> recipeParameterListBodyList = new ArrayList<>();
                recipeBody.setParameterList(recipeParameterListBodyList);

                CarrierInfoDownloadSendBody body = CarrierInfoDownloadSendBody
                        .builder()
                        .equipmentName(carrier.getEquipmentName())
                        .portName(carrier.getPortName())
                        .carrierName(carrier.getCarrierName())
                        .recipe(recipeBody)
                        .build();
                request.setBody(body);

                // RabbitMQ로 메시지 재전송 (MANTI 컴포넌트가 다운되었다 살아났을 때 적체 처리를 보장)
                rabbitTemplate.convertAndSend(
                        RabbitConfig.EXCHANGE_EAS,
                        RabbitConfig.ROUTING_EAS,
                        request
                );

                // 필요 시 이 스케줄러 내부에서 요청 시간을 현재시간으로 업데이트(연장) 처리 하거나
                // 에러 카운트를 올려 시스템 알람을 주는 비즈니스 로직을 여기에 연이어 추가하시면 됩니다.
            }
        }
    }
}