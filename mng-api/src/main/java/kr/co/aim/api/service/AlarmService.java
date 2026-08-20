package kr.co.aim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class AlarmService {
    private final HistoryService historyService;

    // ============== [확인용 코드 추가] ==============
    //    @PostConstruct
    //    public void checkProxy() {
    //        log.info("### Injected AlarmRepository Class: {}", alarmRepository.getClass().getName());
    //    }
    // ===============================================

}