package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.IfEventQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface IfEventQueueJpaRepository extends JpaRepository<IfEventQueueEntity, Long> {

    List<IfEventQueueEntity> findByIfStatusOrderByCreateTimeAsc(String ifStatus);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE IfEventQueueEntity e " +
            "SET e.ifStatus = :newStatus, " +
            "    e.updateTime = :updateTime " +
            "WHERE e.id IN :ids")
    int bulkUpdateIfStatus(@Param("ids") List<Long> ids,
                           @Param("newStatus") String newStatus,
                           @Param("updateTime") LocalDateTime updateTime);
}
