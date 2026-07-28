package kr.co.aim.api.service;

import kr.co.aim.domain.repository.CarrierDefRepository;
import kr.co.aim.domain.repository.CarrierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class ProcessJobService {

    private final CarrierDefRepository carrierDefRepository;
    private final CarrierRepository carrierRepository;


}