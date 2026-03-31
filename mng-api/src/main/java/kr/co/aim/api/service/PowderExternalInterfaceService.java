package kr.co.aim.api.service;

import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderDJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2OrderMJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.H2TransJpaRepository;
import kr.co.aim.infra.persistence.db2springdatajpa.powder.IdocJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"scheduler","simulator"})
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderExternalInterfaceService {

    private final IdocJpaRepository idocJpaRepository;
    private final H2OrderMJpaRepository h2OrderMJpaRepository;
    private final H2OrderDJpaRepository h2OrderDJpaRepository;
    private final H2TransJpaRepository h2TransJpaRepository;

}