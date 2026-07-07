package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.db2springdatajpa.insert.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
@ConditionalOnProperty(name = "factory.type", havingValue = "insert")
public class InsertIdocService {

    private final IdocJpaRepository idocJpaRepository;

    public Long generateNextLineId() {
        // 1번이 비어있다면 즉시 1을 리턴 (AS400/LUW 더미 테이블 체크 우회)
        if (!idocJpaRepository.existsByLineIdOne()) {
            return 1L;
        }
        // 1번이 채워져 있다면 빈틈 중 가장 작은 값 탐색
        return idocJpaRepository.findMinAvailableLineIdAfterOne();
    }
}
