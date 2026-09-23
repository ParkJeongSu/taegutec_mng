package kr.co.aim.infra.persistence.springdatajpa;

import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandEntity;
import kr.co.aim.infra.persistence.entity.WcsSubTransferCommandId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WcsSubTransferCommandJpaRepository extends JpaRepository<WcsSubTransferCommandEntity, WcsSubTransferCommandId> {

    List<WcsSubTransferCommandEntity> findByTransferCommandName(String transferCommandName);

    List<WcsSubTransferCommandEntity> findByTransferCommandNameOrderByJobNoAsc(String transferCommandName);

    List<WcsSubTransferCommandEntity> findByCarrierName(String carrierName);
}
