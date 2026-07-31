package kr.co.aim.api.service;

import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.domain.model.LotCarrierMapping;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "factory.type=powder")
@Transactional // 테스트 완료 후 DB를 자동으로
class CarrierSelectionServiceTest {

    @Autowired
    private CarrierSelectionService carrierSelectionService;

    @Test
    @DisplayName("1. 목표 수량과 정확히 일치하는 조합 선택 검증")
    void testExactMatch() {
        // Given
        BigDecimal targetQty = new BigDecimal("1000.000");
        BigDecimal toleranceQty = new BigDecimal("20.000");

        List<LotCarrierMapping> pool = new ArrayList<>();
        pool.add(createMapping("CARRIER_1", "350.000"));
        pool.add(createMapping("CARRIER_2", "350.000"));
        pool.add(createMapping("CARRIER_3", "300.000"));
        pool.add(createMapping("CARRIER_4", "500.000"));

        // When
        List<LotCarrierMapping> selected = carrierSelectionService.selectCarriers(pool, targetQty, toleranceQty);

        // Then
        assertNotNull(selected);
        assertEquals(3, selected.size());

        BigDecimal sum = BigDecimal.ZERO;
        System.out.println("=== [Test 1] 선택된 캐리어 목록 ===");
        for (int i = 0; i < selected.size(); i++) {
            LotCarrierMapping mapping = selected.get(i);
            System.out.println("캐리어: " + mapping.getCarrierName() + " | 수량: " + mapping.getQuantity());
            sum = sum.add(mapping.getQuantity());
        }
        System.out.println("최종 합계 수량: " + sum);

        assertEquals(0, targetQty.compareTo(sum));
    }

    @Test
    @DisplayName("2. 허용 오차 범위 내(+10) 최적 조합 선택 검증")
    void testMatchWithinTolerance() {
        // Given
        BigDecimal targetQty = new BigDecimal("1000.000");
        BigDecimal toleranceQty = new BigDecimal("20.000"); // 범위: 980.000 ~ 1020.000

        List<LotCarrierMapping> pool = new ArrayList<>();
        pool.add(createMapping("CARRIER_1", "350.000"));
        pool.add(createMapping("CARRIER_2", "350.000"));
        pool.add(createMapping("CARRIER_3", "310.000")); // 350 + 350 + 310 = 1010
        pool.add(createMapping("CARRIER_4", "500.000"));
        pool.add(createMapping("CARRIER_5", "700.000"));

        // When
        List<LotCarrierMapping> selected = carrierSelectionService.selectCarriers(pool, targetQty, toleranceQty);

        // Then
        assertNotNull(selected);
        assertEquals(3, selected.size());

        BigDecimal sum = BigDecimal.ZERO;
        System.out.println("=== [Test 2] 선택된 캐리어 목록 ===");
        for (int i = 0; i < selected.size(); i++) {
            LotCarrierMapping mapping = selected.get(i);
            System.out.println("캐리어: " + mapping.getCarrierName() + " | 수량: " + mapping.getQuantity());
            sum = sum.add(mapping.getQuantity());
        }
        System.out.println("최종 합계 수량: " + sum + " (목표 오차: " + sum.subtract(targetQty) + ")");

        assertEquals(0, new BigDecimal("1010.000").compareTo(sum));
        assertTrue(sum.compareTo(new BigDecimal("980.000")) >= 0);
        assertTrue(sum.compareTo(new BigDecimal("1020.000")) <= 0);
    }

    @Test
    @DisplayName("3. 허용 오차 범위를 벗어날 경우 빈 리스트 반환 검증")
    void testOutOfToleranceReturnsEmpty() {
        // Given
        BigDecimal targetQty = new BigDecimal("1000.000");
        BigDecimal toleranceQty = new BigDecimal("20.000"); // 범위: 980 ~ 1020

        List<LotCarrierMapping> pool = new ArrayList<>();
        pool.add(createMapping("CARRIER_1", "500.000"));
        pool.add(createMapping("CARRIER_2", "600.000")); // 가능 조합: 500, 600, 1100 (모두 오차범위 밖)

        // When
        List<LotCarrierMapping> selected = carrierSelectionService.selectCarriers(pool, targetQty, toleranceQty);

        // Then
        assertNotNull(selected);
        assertTrue(selected.isEmpty(), "오차 범위를 만족하는 조합이 없으므로 빈 리스트가 반환되어야 합니다.");
        System.out.println("=== [Test 3] 조합 실패 처리 확인 (반환 건수: " + selected.size() + ") ===");
    }

    @Test
    @DisplayName("4. 소수점 3자리 정밀도(NUMERIC 15,3) 보정 정밀도 검증")
    void testDecimalPrecision() {
        // Given
        BigDecimal targetQty = new BigDecimal("1000.500");
        BigDecimal toleranceQty = new BigDecimal("10.000");

        List<LotCarrierMapping> pool = new ArrayList<>();
        pool.add(createMapping("CARRIER_1", "350.250"));
        pool.add(createMapping("CARRIER_2", "350.250"));
        pool.add(createMapping("CARRIER_3", "300.000")); // 합: 1000.500

        // When
        List<LotCarrierMapping> selected = carrierSelectionService.selectCarriers(pool, targetQty, toleranceQty);

        // Then
        assertNotNull(selected);
        assertEquals(3, selected.size());

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < selected.size(); i++) {
            sum = sum.add(selected.get(i).getQuantity());
        }
        System.out.println("=== [Test 4] 소수점 조합 결과 합계: " + sum + " ===");

        assertEquals(0, targetQty.compareTo(sum));
    }

    @Test
    @DisplayName("5. 풀 데이터가 비어있거나 목표 수량이 null인 경우 예외 없이 빈 리스트 반환")
    void testEmptyPoolOrNullTarget() {
        // Given
        List<LotCarrierMapping> emptyPool = new ArrayList<>();

        // When
        List<LotCarrierMapping> result1 = carrierSelectionService.selectCarriers(emptyPool, new BigDecimal("1000"), new BigDecimal("10"));
        List<LotCarrierMapping> result2 = carrierSelectionService.selectCarriers(null, new BigDecimal("1000"), new BigDecimal("10"));
        List<LotCarrierMapping> result3 = carrierSelectionService.selectCarriers(emptyPool, null, new BigDecimal("10"));

        // Then
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
        assertTrue(result3.isEmpty());
        System.out.println("=== [Test 5] 방어 로직 정상 동작 확인 ===");
    }

    // 테스트용 LotCarrierMapping 생성 헬퍼 메서드
    private LotCarrierMapping createMapping(String carrierName, String quantityStr) {
        return LotCarrierMapping.builder()
                .id(TsidUtils.nextId())
                .lotName("LOT_TEST_001")
                .carrierName(carrierName)
                .orderId("ORD_20260731_001")
                .orderLineNumber("10")
                .quantity(new BigDecimal(quantityStr))
                .build();
    }

}