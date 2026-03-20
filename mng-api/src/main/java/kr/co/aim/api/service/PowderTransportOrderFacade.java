package kr.co.aim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile({"scheduler","simulator"})
@RequiredArgsConstructor
public class PowderTransportOrderFacade {
    private final TransportOrderService transportOrderService;
    private final PowderExternalInterfaceService powderExternalInterfaceService;

}