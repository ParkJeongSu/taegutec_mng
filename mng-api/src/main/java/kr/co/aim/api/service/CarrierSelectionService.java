package kr.co.aim.api.service;

import kr.co.aim.api.vo.carrier.CarrierDispatchRequestVo;
import kr.co.aim.api.vo.carrier.CarrierSelectionResult;
import kr.co.aim.common.enums.CarrierCleanState;
import kr.co.aim.common.enums.CarrierTransportState;
import kr.co.aim.common.enums.CarrierUseState;
import kr.co.aim.common.enums.ContainerType;
import kr.co.aim.domain.model.Carrier;
import kr.co.aim.domain.model.LotCarrierMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@Profile({"pex","tex","scheduler"})
public class CarrierSelectionService {

    // 소수점 3자리(NUMERIC 15,3) 보정을 위한 스케일 배수
    private static final int SCALE_FACTOR = 1000;

    private final CarrierService carrierService;
    private final ProductionOrderService productionOrderService;

    @Transactional(value = "mssqlTransactionManager")
    public List<CarrierSelectionResult> selectCarrierByInputPort(CarrierDispatchRequestVo vo) {

        // TODO : Input Port
        // Input Port :
        // (1) 설비에서 Production Order Select
        // (2) 존재하면, 해당 order Select
        // (3) 존재하지 않으면, 설비명으로 신규 Production Order Select
        // (4) Order 에서 가장 우선순위가 높은 Carrier Select

        return null;
    }

    @Transactional(value = "mssqlTransactionManager")
    public List<CarrierSelectionResult> selectCarrierByOutputPort(CarrierDispatchRequestVo vo) {
        // TODO : Output Port
        // (1) EquipmentDef 에서 ContainerType을 Select
        // (2) ContainerType None 이거나 위에서 찾은 type으로 가장 우선 순위가 높은 Carrier 찾기
        List<String> containerTypes = new ArrayList<>();
        containerTypes.add(ContainerType.NONE.getValue());
        containerTypes.add(vo.getEquipmentDef().getContainerType());
        List<Carrier> carriers = carrierService.findCarriersForEmptyContainer(
                CarrierCleanState.CLEAN.getValue(),
                CarrierTransportState.IN_WAREHOUSE.getValue(),
                "",
                CarrierUseState.AVAILABLE.getValue(),
                0,
                containerTypes
        );

        // 리스트가 비어있을 수 있으므로 방어 로직 추가
        if (carriers == null || carriers.isEmpty()) {
            return new ArrayList<>();
        }

        List<CarrierSelectionResult> carrierSelectionResultList = new ArrayList<>();
        for(Carrier carrier : carriers) {
            CarrierSelectionResult
                    .builder()
                    .carrier(carrier)
                    .build();
        }

        return carrierSelectionResultList;

    }

    /**
     * DP(0-1 Knapsack) 기반 캐리어 최적 조합 선택
     */
    public List<LotCarrierMapping> selectCarriers(
            List<LotCarrierMapping> availablePool,
            BigDecimal targetQuantity,
            BigDecimal toleranceQuantity
    ) {
        List<LotCarrierMapping> selectedList = new ArrayList<>();

        if (CollectionUtils.isEmpty(availablePool) || targetQuantity == null) {
            return selectedList;
        }

        BigDecimal safeTolerance = (toleranceQuantity != null) ? toleranceQuantity : BigDecimal.ZERO;

        // 1. BigDecimal -> int 스케일 변환
        int targetWeight = scaleToInt(targetQuantity);
        int tolerance = scaleToInt(safeTolerance);
        int maxPossibleWeight = targetWeight + tolerance;

        int n = availablePool.size();

        // dp[j] : j 무게를 만들 수 있는가?
        boolean[] dp = new boolean[maxPossibleWeight + 1];
        // parent[j] : j 무게를 만들 때 사용한 availablePool의 인덱스
        int[] parent = new int[maxPossibleWeight + 1];
        Arrays.fill(parent, -1);

        dp[0] = true;

        // 2. DP 테이블 전개 (0-1 배낭 문제)
        for (int i = 0; i < n; i++) {
            LotCarrierMapping mapping = availablePool.get(i);
            if (mapping.getQuantity() == null) {
                continue;
            }

            int currentWeight = scaleToInt(mapping.getQuantity());

            // 용량을 초과하는 항목은 제외
            if (currentWeight > maxPossibleWeight || currentWeight <= 0) {
                continue;
            }

            // 중복 선택 방지를 위한 역순 루프
            for (int j = maxPossibleWeight; j >= currentWeight; j--) {
                if (!dp[j] && dp[j - currentWeight]) {
                    dp[j] = true;
                    parent[j] = i;
                }
            }
        }

        // 3. 허용 오차 범위 내에서 target과 가장 가까운 최적 무게 탐색
        int bestWeight = -1;
        int minDiff = Integer.MAX_VALUE;
        int startWeight = Math.max(0, targetWeight - tolerance);

        for (int w = startWeight; w <= maxPossibleWeight; w++) {
            if (dp[w]) {
                int diff = Math.abs(w - targetWeight);
                if (diff < minDiff) {
                    minDiff = diff;
                    bestWeight = w;
                }
            }
        }

        // 조건에 맞는 조합을 찾지 못한 경우
        if (bestWeight == -1) {
            log.warn("DP allocation failed: No carrier combination found within target: {} (+/- {})",
                    targetQuantity, safeTolerance);
            return selectedList;
        }

        // 4. 결과 역추적 (Backtracking DP Path)
        int curr = bestWeight;
        while (curr > 0 && parent[curr] != -1) {
            int carrierIdx = parent[curr];
            LotCarrierMapping selectedMapping = availablePool.get(carrierIdx);
            selectedList.add(selectedMapping);

            int usedWeight = scaleToInt(selectedMapping.getQuantity());
            curr -= usedWeight;
        }

        return selectedList;
    }

    private int scaleToInt(BigDecimal value) {
        if (value == null) {
            return 0;
        }
        return value.setScale(3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(SCALE_FACTOR))
                .intValue();
    }


}