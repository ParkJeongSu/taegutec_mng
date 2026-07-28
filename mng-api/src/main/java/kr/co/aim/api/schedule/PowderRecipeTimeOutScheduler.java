package kr.co.aim.api.schedule;

import kr.co.aim.api.service.CarrierService;
import kr.co.aim.api.service.LotCarrierMappingService;
import kr.co.aim.common.Utils.FormatUtils;
import kr.co.aim.common.enums.*;
import kr.co.aim.common.format.*;
import kr.co.aim.common.format.request.BaseMessage;
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
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"scheduler"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderRecipeTimeOutScheduler {

    private final RabbitTemplate rabbitTemplate;
    private final LotCarrierMappingService lotCarrierMappingService;
    private final CarrierService carrierService;

    /**
     * 2분마다 실행 (120,000ms)
     * MANTI 레시피 서버로 요청을 보낸 뒤 2분이 지나도록 Reply가 없는 캐리어를 구출하거나 이력을 재발행합니다.
     */
    @Scheduled(fixedDelay = 120000)
    @SchedulerLock(name = "powderRecipeRequestTimeOut",
            lockAtMostFor = "PT1M45S",   // 스케줄 간격(2분)보다 살짝 작게 잡아 락 겹침 방지
            lockAtLeastFor = "PT10S")
    public void powderRecipeRequestTimeOut() {
        log.info("### MANTI Timeout Detection Scheduler Started ###");

        // 현재 시간 기준 2분 전 시점 계산 (Threshold)
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(2);

        // WAIT 상태이면서 요청한 지 2분이 지났고, REPLY 시간은 비어있는 타겟 추출
        List<LotCarrierMapping> timeoutList = lotCarrierMappingService.findMantiTimeoutTargets(
                MantiRequestState.WAIT.getValue(),
                thresholdTime
        );

        if (CollectionUtils.isNotEmpty(timeoutList)) {
            log.warn("[MANTI TIMEOUT] 무응답 타겟 {} 건 발견.", timeoutList.size());

            for (LotCarrierMapping mapping : timeoutList) {
                try {
                    log.warn("-> 타임아웃 디테일 - Carrier: {}, Lot: {}, MNG_KEY: {}, RequestTime: {}",
                            mapping.getCarrierName(), mapping.getLotName(), mapping.getMngKey(), mapping.getMantiRequestTime());

                    // 3. MQ 메시지 생성 및 발송
                    String transactionId = FormatUtils.generateTransactionId();
                    BaseMessage<RecipeTimeOutRequestBody> request = new BaseMessage<>();
                    request.setMessageName(MessageList.RECIPE_TIME_OUT_REQUEST.getMessageName());
                    request.setMessageFrom(SystemName.MNG.getValue());
                    request.setMessageOwner(SystemName.MNG.getValue());
                    request.setMessageTo(SystemName.MNG.getValue());
                    request.setResultCode(ResultCode.OK.getValue());
                    request.setTransactionId(transactionId);

                    RecipeTimeOutRequestBody body = RecipeTimeOutRequestBody.builder()
                            .id(mapping.getId())
                            .lotName(mapping.getLotName())
                            .carrierName(mapping.getCarrierName())
                            .orderId(mapping.getOrderId())
                            .orderLineNumber(mapping.getOrderLineNumber())
                            .productionOrderId(mapping.getProductionOrderId())
                            .productionStatus(mapping.getProductionStatus())
                            .processStatus(mapping.getProcessStatus())
                            .quantity(mapping.getQuantity())
                            .galQuantity(mapping.getGalQuantity())
                            .mngKey(mapping.getMngKey())
                            .jobStartTime(mapping.getJobStartTime())
                            .jobEndTime(mapping.getJobEndTime())
                            .mantiRequestState(mapping.getMantiRequestState())
                            .mantiRequestTime(mapping.getMantiRequestTime())
                            .mantiReplyTime(mapping.getMantiReplyTime())
                            .rrnRequestState(mapping.getRrnRequestState())
                            .rrnRequestTime(mapping.getRrnRequestTime())
                            .rrnReplyTime(mapping.getRrnReplyTime())
                            .holdState(mapping.getHoldState())
                            .reasonCode(mapping.getReasonCode())
                            .eventName(mapping.getEventName())
                            .eventTime(mapping.getEventTime())
                            .eventUser(mapping.getEventUser())
                            .eventComment(mapping.getEventComment())
                            .build();

                    request.setBody(body);

                    rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_PEX, RabbitConfig.ROUTING_PEX, request);
                    log.info("recipe timeout message sent for id {}", mapping.getId());
                }
                catch (Exception e) {
                    log.error("Failed to process allocate request for ProductionOrder ID: {}", mapping.getId(), e);
                }
            }
        }
    }
}