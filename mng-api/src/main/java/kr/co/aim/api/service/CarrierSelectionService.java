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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarrierSelectionService {

    private static final BigDecimal LARGE_ORDER_THRESHOLD = new BigDecimal("100000");
    private static final int DEFAULT_SCALE_FACTOR = 1000;
    private static final int LARGE_ORDER_SCALE_FACTOR = 1;

    /**
     * 일반 수량(Quantity) 기반 캐리어 최적 조합 선택
     */
    public List<LotCarrierMapping> selectBestCarriers(
            List<LotCarrierMapping> availablePool,
            BigDecimal targetQuantity,
            BigDecimal toleranceQuantity
    ) {
        BigDecimal safeTolerance = (toleranceQuantity != null) ? toleranceQuantity : BigDecimal.ZERO;
        return runKnapsack(availablePool, targetQuantity, safeTolerance, LotCarrierMapping::getQuantity);
    }

    /**
     * ERP 수량(GalQuantity) 기반 캐리어 최적 조합 선택
     */
    public List<LotCarrierMapping> selectBestCarriersByGalQuantity(
            List<LotCarrierMapping> availablePool,
            BigDecimal targetQuantity
    ) {
        return runKnapsack(availablePool, targetQuantity, BigDecimal.ZERO, LotCarrierMapping::getGalQuantity);
    }

    /**
     * 공통 0-1 Knapsack core 알고리즘
     */
    private List<LotCarrierMapping> runKnapsack(
            List<LotCarrierMapping> availablePool,
            BigDecimal targetQuantity,
            BigDecimal toleranceQuantity,
            Function<LotCarrierMapping, BigDecimal> quantityExtractor
    ) {
        List<LotCarrierMapping> selectedList = new ArrayList<>();

        if (CollectionUtils.isEmpty(availablePool) || targetQuantity == null) {
            return selectedList;
        }

        int scaleFactor = (targetQuantity.compareTo(LARGE_ORDER_THRESHOLD) >= 0)
                ? LARGE_ORDER_SCALE_FACTOR
                : DEFAULT_SCALE_FACTOR;

        int targetWeight = scaleToInt(targetQuantity, scaleFactor);
        int tolerance = scaleToInt(toleranceQuantity, scaleFactor);
        int maxPossibleWeight = targetWeight + tolerance;

        int n = availablePool.size();
        boolean[] dp = new boolean[maxPossibleWeight + 1];
        int[] parent = new int[maxPossibleWeight + 1];
        Arrays.fill(parent, -1);

        dp[0] = true;

        for (int i = 0; i < n; i++) {
            LotCarrierMapping mapping = availablePool.get(i);
            BigDecimal rawQty = quantityExtractor.apply(mapping);
            if (rawQty == null) {
                continue;
            }

            int currentWeight = scaleToInt(rawQty, scaleFactor);
            if (currentWeight > maxPossibleWeight || currentWeight <= 0) {
                continue;
            }

            for (int j = maxPossibleWeight; j >= currentWeight; j--) {
                if (!dp[j] && dp[j - currentWeight]) {
                    dp[j] = true;
                    parent[j] = i;
                }
            }
        }

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

        if (bestWeight == -1) {
            log.warn("DP allocation failed: Target={}", targetQuantity);
            return selectedList;
        }

        int curr = bestWeight;
        while (curr > 0 && parent[curr] != -1) {
            int carrierIdx = parent[curr];
            LotCarrierMapping selectedMapping = availablePool.get(carrierIdx);
            selectedList.add(selectedMapping);

            int usedWeight = scaleToInt(quantityExtractor.apply(selectedMapping), scaleFactor);
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