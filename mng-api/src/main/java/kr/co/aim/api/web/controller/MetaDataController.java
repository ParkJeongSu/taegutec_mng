package kr.co.aim.api.web.controller;

import kr.co.aim.api.service.MetaDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/meta-data")
@RequiredArgsConstructor
public class MetaDataController {
    private final MetaDataService metaDataService;

    /**
     * 동적 Enum 조회 API
     * 예: GET /api/meta-data/alarm-type
     * GET /api/meta-data/alarm-state
     */
    @GetMapping("/{enumKey}")
    public ResponseEntity<Page<Map<String, String>>> getMetaData(@PathVariable String enumKey) {
        try {
            //List<String> data = metaDataService.getMetaDataList(enumKey);
            List<Map<String, String>> dataList = metaDataService.getMetaData(enumKey);
            long total = dataList.size();

            Page<Map<String, String>> dataPage = new PageImpl<>(dataList, Pageable.unpaged(), total);
            return ResponseEntity.ok(dataPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 동적 Enum 조회 API
     * 예: GET /api/meta-data/carrier-type/container
     * GET /api/meta-data/carrier-type/container
     */
    @GetMapping("/{enumKey}/{enumValue}")
    public ResponseEntity<Page<Map<String, String>>> getMetaData(@PathVariable String enumKey,@PathVariable String enumValue) {
        try {
            //List<String> data = metaDataService.getMetaDataList(enumKey);
            List<Map<String, String>> dataList = metaDataService.getMetaData(enumKey,enumValue);
            long total = dataList.size();

            Page<Map<String, String>> dataPage = new PageImpl<>(dataList, Pageable.unpaged(), total);
            return ResponseEntity.ok(dataPage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * (선택 사항) 현재 등록된 모든 메타데이터 키 목록 조회
     * 예: GET /api/meta-data
     * -> ["alarm-type", "alarm-state"]
     */
    @GetMapping
    public ResponseEntity<Set<String>> listAllMetaDataKeys() {
        return ResponseEntity.ok(metaDataService.getAllMetaDataKeys());
    }
}
