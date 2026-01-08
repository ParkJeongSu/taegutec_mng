package kr.co.aim.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HistoryMetaService {

    private final Map<String, List<String>> systemResourceMap;

    public HistoryMetaService(@Qualifier("historySystemMap") Map<String, List<String>> systemResourceMap ) {
        this.systemResourceMap = systemResourceMap;
    }

    /**
     * 특정 시스템에서 조회 가능한 히스토리 리소스 목록을 반환
     */
    public List<String> getResourcesBySystem(String systemName) {
        // "MES" 키로 맵에서 리스트를 찾아 반환 (없으면 빈 리스트)
        return systemResourceMap.getOrDefault(systemName.toUpperCase(), List.of());
    }

}
