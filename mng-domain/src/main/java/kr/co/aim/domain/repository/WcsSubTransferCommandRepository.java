package kr.co.aim.domain.repository;

import kr.co.aim.common.condition.WcsSubTransferCommandSearchCondition;
import kr.co.aim.domain.model.WcsSubTransferCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WcsSubTransferCommandRepository {

    List<WcsSubTransferCommand> findAll();

    Optional<WcsSubTransferCommand> findById(String transferCommandName, Integer jobNo);

    List<WcsSubTransferCommand> findByTransferCommandName(String transferCommandName);

    List<WcsSubTransferCommand> findByCarrierName(String carrierName);

    boolean existsById(String transferCommandName, Integer jobNo);

    WcsSubTransferCommand save(WcsSubTransferCommand command);

    void deleteById(String transferCommandName, Integer jobNo);

    Page<WcsSubTransferCommand> findSubTransferCommands(WcsSubTransferCommandSearchCondition condition, Pageable pageable);
}
