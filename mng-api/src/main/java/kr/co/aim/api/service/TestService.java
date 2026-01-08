package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.entity.TableA;
import kr.co.aim.infra.persistence.entity.TableB;
import kr.co.aim.infra.persistence.springdatajpa.TableARepository;
import kr.co.aim.infra.persistence.springdatajpa.TableBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TableARepository tableARepository;
    private final TableBRepository tableBRepository;

    @Transactional
    public void testCrossSchemaTransaction() {
        // 1. dbo.TableA에 저장
        tableARepository.save(new TableA(1));

        // 2. TestSchema.TableB에 저장
        tableBRepository.save(new TableB(100));

        // 3. 의도적 예외 발생 (트랜잭션 롤백 확인용)
        if (true) {
            throw new RuntimeException("트랜잭션 테스트용 예외 발생!");
        }
    }
}
