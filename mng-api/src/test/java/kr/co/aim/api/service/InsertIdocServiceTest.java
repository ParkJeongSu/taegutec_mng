package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.db2entity.insert.IdocEntity;
import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "factory.type=insert")
@Transactional // 테스트 완료 후 DB를 자동으로
// 롤백하여 기존 데이터에 영향을 주지 않음
@ActiveProfiles({"scheduler"}) // 환경에 맞는 프로파일 설정 (로컬 LUW 연결용)
@Disabled
class InsertIdocServiceTest {

    @Autowired
    private InsertIdocService insertIdocService;

    @Autowired
    private IdocJpaRepository idocJpaRepository;

    @Test
    void 테이블이_비어있거나_1번이_없으면_1을_반환한다() {
        // Given: 테이블이 비어있는 상태 (또는 1번이 없는 상태)
        // @Transactional 덕분에 매 테스트마다 깨끗한 상태 혹은 롤백이 보장됩니다.

        // When: ID 발행 요청
        Long nextId = insertIdocService.generateNextLineId();

        // Then: 결과가 1인지 검증 (System.out 대신 assertEquals 사용)
        assertEquals(1L, nextId, "테이블이 비어있을 때는 1이 발행되어야 합니다.");
    }

    @Test
    void 중간에_빈틈이_있을때_가장_작은_빈값을_반환한다() {
        // Given: 1, 2, 3, 4, 7, 10 데이터 강제 적재 (5, 6이 비어있는 상황)
        saveDummyIdoc(1L);
        saveDummyIdoc(2L);
        saveDummyIdoc(3L);
        saveDummyIdoc(4L);
        saveDummyIdoc(7L);
        saveDummyIdoc(10L);

        // When: ID 발행 요청
        Long nextId = insertIdocService.generateNextLineId();

        // Then: 기대값은 비어있는 값 중 가장 작은 '5'여야 함
        assertEquals(5L, nextId, "1,2,3,4,7,10이 있을 때 가장 작은 빈값은 5여야 합니다.");
    }

    // 테스트용 데이터 생성을 위한 헬퍼 메서드
    private void saveDummyIdoc(Long lineId) {
        IdocEntity entity = IdocEntity.builder()
                .lineId(lineId)
                // .idocTypId(100L) 등 다른 필수 컬럼이 있다면 여기에 세팅
                .build();
        idocJpaRepository.save(entity);
    }
}