package kr.co.aim.infra.persistence.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.aim.common.handler.IBaseHistoryEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
public class GenericHistoryRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * 제네릭 히스토리 저장 메서드
     */
    @Transactional
    public <T extends IBaseHistoryEntity> T save(T historyEntity) {
        em.persist(historyEntity);
        return historyEntity;
    }

}
