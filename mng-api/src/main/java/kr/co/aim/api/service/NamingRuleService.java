package kr.co.aim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다. (DI)
@Slf4j
public class NamingRuleService {


    public String getTransportJobName(String systemName, LocalDateTime eventTime){

        // 1. 원하는 패턴 정의 (G_20260409150200000 형태를 위한 패턴)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

        // 2. LocalDateTime을 문자열로 변환
        String formattedTime = eventTime.format(formatter);

        // 3. 접두어와 언더바(_) 결합
        return systemName.charAt(0) + "_" + formattedTime;
    }

}