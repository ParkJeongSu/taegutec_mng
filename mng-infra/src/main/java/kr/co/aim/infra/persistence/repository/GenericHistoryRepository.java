package kr.co.aim.infra.persistence.repository;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.lang.reflect.Field;

@Repository
public class GenericHistoryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 제네릭 히스토리 조회 메서드
     * @param historyEntityClass  조회할 엔티티 클래스 (예: AlarmHistoryEntity.class)
     * @param searchConditionDto    검색 조건 (예: "equipmentName", "RUN")
     * @param pageable            페이징 정보
     * @param <T>                 IBaseHistoryEntity를 구현한 엔티티 타입
     * @return
     */
    public <T extends IBaseHistoryEntity> Page<T> findHistory(
            Class<T> historyEntityClass,
            Object searchConditionDto, // DTO를 Object로 받음
            Pageable pageable
    ) {
        String entityName = historyEntityClass.getSimpleName();

        StringBuilder jpql = new StringBuilder("SELECT h FROM " + entityName + " h WHERE 1=1");
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(h) FROM " + entityName + " h WHERE 1=1");

        // DTO를 파싱하여 파라미터 맵 생성
        Map<String, Object> parameters = new HashMap<>();

        if (searchConditionDto != null) {
            // 리플렉션을 사용하여 DTO의 필드를 동적으로 순회
            // [수정] DTO의 모든 필드 (부모 클래스 포함)를 가져옴
            List<Field> allFields = getAllFields(searchConditionDto.getClass());

            for (Field field : allFields) {
                try {
                    field.setAccessible(true); // private 필드에 접근 허용
                    Object value = field.get(searchConditionDto); // 필드의 값 읽기

                    // 값이 null이 아니거나 빈 문자열이 아닌 경우에만 조건 추가
                    if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
                        String fieldName = field.getName();

                        // ----- [수정된 로직] -----
                        if (fieldName.equals("fromDate")) {
                            // 'fromDate'는 'eventTime' 컬럼을 >= 로 검색
                            jpql.append(" AND h.eventTime >= :fromDate");
                            countJpql.append(" AND h.eventTime >= :fromDate");
                            parameters.put(fieldName, value);

                        } else if (fieldName.equals("toDate")) {
                            // 🚨 [수정된 로직]
                            Date toDate = (Date) value;

                            // Date를 Calendar로 변환하여 시간 확인
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(toDate);

                            // 시간이 00:00:00인지 (즉, type="date"로 입력되었는지) 확인
                            if (cal.get(Calendar.HOUR_OF_DAY) == 0 &&
                                    cal.get(Calendar.MINUTE) == 0 &&
                                    cal.get(Calendar.SECOND) == 0)
                            {
                                // 1. type="date"인 경우: 1일을 더하고 "<" (미만)으로 비교
                                // (예: 2025-10-28 입력 -> 2025-10-29 00:00:00 '미만'으로 검색)
                                cal.add(Calendar.DATE, 1);
                                Date nextDay = cal.getTime();

                                jpql.append(" AND h.eventTime < :toDateExclusive");
                                countJpql.append(" AND h.eventTime < :toDateExclusive");
                                parameters.put("toDateExclusive", nextDay);
                            } else {
                                // 2. type="datetime-local"인 경우: (정확한 시간까지) "<=" (이하)로 비교
                                // (예: 2025-10-28 15:35 입력 -> 2025-10-28 15:35:00 '이하'로 검색)
                                jpql.append(" AND h.eventTime <= :toDateInclusive");
                                countJpql.append(" AND h.eventTime <= :toDateInclusive");
                                parameters.put("toDateInclusive", toDate);
                            }
                            // ----- [수정 끝] -----

                        } else {
                            // 그 외의 모든 필드는 기존처럼 = 로 검색
                            jpql.append(" AND h.").append(fieldName).append(" = :").append(fieldName);
                            countJpql.append(" AND h.").append(fieldName).append(" = :").append(fieldName);
                            parameters.put(fieldName, value);
                        }
                        // ----- [수정 끝] -----
                    }
                } catch (IllegalAccessException e) {
                    // 예외 처리 (e.g., 로깅)
                    throw new RuntimeException("Failed to read search condition DTO", e);
                }
            }
        }

        jpql.append(" ORDER BY h.eventTime DESC");

        // 쿼리 생성
        TypedQuery<T> dataQuery = em.createQuery(jpql.toString(), historyEntityClass);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);

        // 파라미터 바인딩
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            dataQuery.setParameter(param.getKey(), param.getValue());
            countQuery.setParameter(param.getKey(), param.getValue());
        }

        // !! ===== [수정됨] 페이지네이션 적용 ===== !!
        // Pageable의 offset (page * size) 정보를 기반으로 시작 위치 설정
        dataQuery.setFirstResult((int) pageable.getOffset());
        // Pageable의 size 정보를 기반으로 가져올 개수 설정
        dataQuery.setMaxResults(pageable.getPageSize());
        // !! ====================================== !!

        // ... (페이징 및 반환 로직은 동일)
        // ...
        List<T> content = dataQuery.getResultList();
        long total = countQuery.getSingleResult();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 제네릭 히스토리 저장 메서드
     * (이것은 어제와 동일하게 작동합니다)
     */
    @Transactional
    public <T extends IBaseHistoryEntity> T save(T historyLog) {
        em.persist(historyLog);
        return historyLog;
    }

    /**
     * [신규 헬퍼 메서드]
     * 상속받은 모든 부모 클래스의 private 필드까지 재귀적으로 가져옵니다.
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass(); // 부모 클래스로 이동
        }

        return fields;
    }
}
