package kr.co.aim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile({"scheduler","simulator"})
@RequiredArgsConstructor
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class PowderTransportOrderFacade {
    private final PowderExternalInterfaceService powderExternalInterfaceService;
}