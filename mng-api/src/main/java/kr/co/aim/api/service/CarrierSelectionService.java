package kr.co.aim.api.service;

import kr.co.aim.domain.model.LotCarrierMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
@Profile({"pex","tex","scheduler"})
public class CarrierSelectionService {

    // 100톤 기준 수량 ( 단위: kg, 100,000 kg )
    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("100000");
    private static final int DEFAULT_SCALE_FACTOR = 1000; // 일반 오더 (소수점 3자리 보정)
    private static final int LARGE_ORDER_SCALE_FACTOR = 1;  // 100톤 이상 대형 오더 (1kg 단위)

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

        // 100톤 이상 여부에 따라 SCALE_FACTOR 동적 결정
        int scaleFactor = DEFAULT_SCALE_FACTOR;
        if (targetQuantity.compareTo(LARGE_ORDER_THRESHOLD) >= 0) {
            scaleFactor = LARGE_ORDER_SCALE_FACTOR;
            log.info("Large order detected (Target: {} >= 100t). SCALE_FACTOR adjusted to {}", targetQuantity, scaleFactor);
        }

        BigDecimal safeTolerance = (toleranceQuantity != null) ? toleranceQuantity : BigDecimal.ZERO;

        // 1. BigDecimal -> int 스케일 변환 (결정된 scaleFactor 전달)
        int targetWeight = scaleToInt(targetQuantity, scaleFactor);
        int tolerance = scaleToInt(safeTolerance, scaleFactor);
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

            int currentWeight = scaleToInt(mapping.getQuantity(), scaleFactor);

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

            int usedWeight = scaleToInt(selectedMapping.getQuantity(), scaleFactor);
            curr -= usedWeight;
        }

        return selectedList;
    }

    private int scaleToInt(BigDecimal value, int scaleFactor) {
        if (value == null) {
            return 0;
        }
        return value.setScale(3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(scaleFactor))
                .intValue();
    }
}