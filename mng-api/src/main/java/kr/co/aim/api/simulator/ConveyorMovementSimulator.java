package kr.co.aim.api.simulator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Component
@EnableScheduling
@Slf4j
@Profile({"web"})
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class ConveyorMovementSimulator {

    private final SimpMessagingTemplate messagingTemplate;

    // 워크스테이션별 이동 경로 정의 구조체
    private static class WorkStationRoute {
        String warehouseId;
        String workStationId;
        String[] path;

        WorkStationRoute(String warehouseId, String workStationId, String[] path) {
            this.warehouseId = warehouseId;
            this.workStationId = workStationId;
            this.path = path;
        }
    }

    // 워크스테이션 내에서 동시 이동 중인 개별 캐리어 상태
    @Getter
    @Setter
    private static class ActiveTrayState {
        String carrierName;
        int stepIndex; // 0 이상이면 이동 중, 음수이면 순차 진입 대기

        ActiveTrayState(String carrierName, int stepIndex) {
            this.carrierName = carrierName;
            this.stepIndex = stepIndex;
        }
    }

    private final List<WorkStationRoute> routes = new ArrayList<>();
    private final List<List<ActiveTrayState>> wsTrayGroups = new ArrayList<>();
    private int carrierSequence = 1;

    public ConveyorMovementSimulator(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;

        // ==========================================
        // 1. WH1 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("1", "311", new String[]{
                "WH1-31101", "311-31101", "311-31104", "311-31111", "311-31114", "WH1-31114"
        }));
        routes.add(new WorkStationRoute("1", "312", new String[]{
                "WH1-31201", "312-31201", "312-31204", "312-31211", "312-31214", "WH1-31214"
        }));
        routes.add(new WorkStationRoute("1", "313", new String[]{
                "WH1-31303", "313-31301", "313-31303", "313-31311", "313-31313", "WH1-31313"
        }));
        routes.add(new WorkStationRoute("1", "314", new String[]{
                "WH1-31404", "314-31401", "314-31404", "314-31411", "314-31414", "WH1-31414"
        }));
        routes.add(new WorkStationRoute("1", "315", new String[]{
                "WH1-31501", "315-31501", "315-31508", "315-31511", "315-31518", "WH1-31518"
        }));
        routes.add(new WorkStationRoute("1", "316", new String[]{
                "WH1-31601", "316-31601", "316-31603"
        }));

        // ==========================================
        // 2. WH2 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("2", "321", new String[]{
                "WH2-32101", "321-32101", "321-32104", "321-32111", "321-32114", "WH2-32114"
        }));
        routes.add(new WorkStationRoute("2", "322", new String[]{
                "WH2-32201", "322-32201", "322-32204", "322-32211", "322-32214", "WH2-32214"
        }));
        routes.add(new WorkStationRoute("2", "323", new String[]{
                "WH2-32301", "323-32301", "323-32304", "323-32311", "323-32314", "WH2-32314"
        }));
        routes.add(new WorkStationRoute("2", "324", new String[]{
                "WH2-32401", "324-32401", "324-32403", "324-32405", "324-32407", "WH2-32407"
        }));
        routes.add(new WorkStationRoute("2", "325", new String[]{
                "WH2-32501", "325-32501", "325-32503", "325-32505", "325-32507", "WH2-32507"
        }));
        routes.add(new WorkStationRoute("2", "326", new String[]{
                "WH2-32601", "326-32601", "326-32605", "326-32611", "326-32612", "WH2-32612"
        }));

        // ==========================================
        // 3. WH3 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("3", "331", new String[]{
                "WH3-33101", "331-33101", "331-33103", "331-33105", "331-33107", "WH3-33107"
        }));

        // ==========================================
        // 4. WH4 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("4", "341", new String[]{
                "WH4-34101", "341-34101", "341-34103", "341-34105", "341-34107", "WH4-34107"
        }));
        routes.add(new WorkStationRoute("4", "342", new String[]{
                "WH4-34201", "342-34201", "342-34204", "342-34211", "342-34214", "WH4-34214"
        }));
        routes.add(new WorkStationRoute("4", "343", new String[]{
                "WH4-34301", "343-34301", "343-34303", "343-34305", "343-34307", "WH4-34307"
        }));
        routes.add(new WorkStationRoute("4", "344", new String[]{
                "WH4-34401", "344-34401", "344-34404", "344-34411", "344-34414", "WH4-34414"
        }));

        // ==========================================
        // 5. WH5 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("5", "351", new String[]{
                "WH5-35101", "351-35101", "351-35103", "351-35105", "351-35107", "WH5-35107"
        }));
        routes.add(new WorkStationRoute("5", "352", new String[]{
                "WH5-35201", "352-35201", "352-35204", "352-35211", "352-35214", "WH5-35214"
        }));

        // ==========================================
        // 6. WH6 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("6", "361", new String[]{
                "WH6-36101", "361-36101", "361-36105", "361-36107", "361-36111", "WH6-36111"
        }));
        routes.add(new WorkStationRoute("6", "362", new String[]{
                "WH6-36201", "362-36201", "362-36207", "362-36211", "362-36217", "WH6-36217"
        }));
        routes.add(new WorkStationRoute("6", "363", new String[]{
                "WH6-36301", "363-36301", "363-36303", "363-36305", "363-36307", "WH6-36307"
        }));

        // ==========================================
        // 7. WH7 워크스테이션 경로 매핑
        // ==========================================
        routes.add(new WorkStationRoute("7", "371", new String[]{
                "WH7-37101", "371-37101", "371-37104", "371-37111", "371-37113", "WH7-37113"
        }));
        routes.add(new WorkStationRoute("7", "372", new String[]{
                "WH7-37201", "372-37201", "372-37203", "372-37205", "372-37207", "WH7-37207"
        }));

        // 모든 워크스테이션에 2~3개의 캐리어가 겹치지 않고 연속 주행하도록 초기 오프셋 배치
        for (int i = 0; i < routes.size(); i++) {
            WorkStationRoute r = routes.get(i);
            List<ActiveTrayState> trayList = new ArrayList<>();

            int pathLength = r.path.length;
            int midPos = pathLength > 2 ? 2 : 1;

            // 1번 캐리어: 경로 중간에서 시작
            trayList.add(new ActiveTrayState("TRAY-" + r.workStationId + "-01", midPos));
            // 2번 캐리어: 경로 출발점에서 시작
            trayList.add(new ActiveTrayState("TRAY-" + r.workStationId + "-02", 0));
            // 3번 캐리어: 2주기 후 출발 대기
            trayList.add(new ActiveTrayState("TRAY-" + r.workStationId + "-03", -2));

            wsTrayGroups.add(trayList);
        }
    }

    /**
     * 3초마다 모든 워크스테이션(총 23개 구역)의 활성 캐리어를 순차 전진 전송
     */
    @Scheduled(fixedRate = 3000)
    public void simulateAllWorkStationTrays() {
        for (int w = 0; w < routes.size(); w++) {
            WorkStationRoute route = routes.get(w);
            List<ActiveTrayState> trays = wsTrayGroups.get(w);
            String destinationTopic = "/topic/warehouse/" + route.warehouseId + "/conveyor";

            for (int t = 0; t < trays.size(); t++) {
                ActiveTrayState tray = trays.get(t);

                // 진입 대기 중인 트레이의 카운트 감소
                if (tray.getStepIndex() < 0) {
                    tray.setStepIndex(tray.getStepIndex() + 1);
                    continue;
                }

                // 경로 종료 지점에 도달한 경우 (배출 후 초기화)
                if (tray.getStepIndex() >= route.path.length) {
                    String lastPos = route.path[route.path.length - 1];

                    ConveyorTrayMovementDto removeDto = ConveyorTrayMovementDto.builder()
                            .warehouseId(route.warehouseId)
                            .workStationId(route.workStationId)
                            .carrierName(tray.getCarrierName())
                            .positionId(lastPos)
                            .previousPosition(lastPos)
                            .action("REMOVE")
                            .state("COMPLETED")
                            .eventTime(LocalDateTime.now())
                            .build();

                    this.messagingTemplate.convertAndSend(destinationTopic, removeDto);

                    // 새로운 캐리어 번호로 갱신하여 0번 지점에서 재투입
                    carrierSequence++;
                    String nextCarrierName = "TRAY-" + route.workStationId + "-" + String.format("%02d", (carrierSequence % 99) + 1);
                    tray.setCarrierName(nextCarrierName);
                    tray.setStepIndex(0);
                    continue;
                }

                // 일반 이동 (ENTER / MOVE)
                String currentPos = route.path[tray.getStepIndex()];
                String prevPos = (tray.getStepIndex() > 0) ? route.path[tray.getStepIndex() - 1] : null;
                String action = (tray.getStepIndex() == 0) ? "ENTER" : "MOVE";

                ConveyorTrayMovementDto moveDto = ConveyorTrayMovementDto.builder()
                        .warehouseId(route.warehouseId)
                        .workStationId(route.workStationId)
                        .carrierName(tray.getCarrierName())
                        .positionId(currentPos)
                        .previousPosition(prevPos)
                        .action(action)
                        .state("MOVING")
                        .eventTime(LocalDateTime.now())
                        .build();

                this.messagingTemplate.convertAndSend(destinationTopic, moveDto);

                tray.setStepIndex(tray.getStepIndex() + 1);
            }
        }
    }
}