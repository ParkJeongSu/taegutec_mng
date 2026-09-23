package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsTransferCommandSearchCondition;
import kr.co.aim.domain.model.WcsTransferCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsTransferCommandRepository {

    List<WcsTransferCommand> findAll();

    Optional<WcsTransferCommand> findById(String transferCommandName);

    List<WcsTransferCommand> findByFactoryName(String factoryName);

    List<WcsTransferCommand> findByCarrierName(String carrierName);

    boolean existsById(String transferCommandName);

    WcsTransferCommand save(WcsTransferCommand command);

    void deleteById(String transferCommandName);

    Page<WcsTransferCommand> findTransferCommands(WcsTransferCommandSearchCondition condition, Pageable pageable);
}
