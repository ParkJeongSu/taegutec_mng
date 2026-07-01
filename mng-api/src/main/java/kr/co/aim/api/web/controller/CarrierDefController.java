package kr.co.aim.api.web.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.CarrierDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.dto.CarrierDefSaveRequestDto;
import kr.co.aim.common.dto.CarrierDefSearchConditionDto;
import kr.co.aim.domain.model.CarrierDef;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "MNG Carrier Definition", description = "기준정보 Carrier Def 관리 API")
@RestController
@RequestMapping("/api/v1/mng/carrier-def")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class CarrierDefController {

    private final CarrierDefService carrierDefService;

    @Operation(summary = "Carrier Def 목록 조회", description = "동적 조건 및 페이징 처리를 통한 Carrier 기준정보 조회")
    @GetMapping("")
    public ResponseEntity<Page<CarrierDef>> getCarrierDefs(
            CarrierDefSearchConditionDto condition,
            @ParameterObject Pageable pageable
    ) {
        Page<CarrierDef> result = carrierDefService.findCarrierDefWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Carrier Def 상세 조회", description = "ID를 이용한 단건 세부 내역 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<CarrierDef>> getCarrierDef(@PathVariable("id") Long id) {
        CarrierDef result = carrierDefService.findById(id);
        List<CarrierDef> carrierDefList = Collections.singletonList(result);
        return ResponseEntity.ok(new PageImpl<>(carrierDefList, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Carrier Def 생성", description = "신규 Carrier 기준정보 마스터 등록")
    @PostMapping("")
    public ResponseEntity<Page<CarrierDef>> createCarrierDef(@RequestBody CarrierDefSaveRequestDto dto) {
        CarrierDef createdCarrierDef = carrierDefService.createCarrierDef(dto);
        List<CarrierDef> carrierDefList = Collections.singletonList(createdCarrierDef);
        return ResponseEntity.ok(new PageImpl<>(carrierDefList,PageRequest.of(0, 1), 1) );
    }

    @Operation(summary = "Carrier Def 수정", description = "기존 일련번호 기준정보 내역 변경 및 추적 이벤트 갱신")
    @PutMapping("/{id}")
    public ResponseEntity<Page<CarrierDef>> updateCarrierDef(
            @PathVariable("id") Long id,
            @RequestBody CarrierDefSaveRequestDto dto
    ) {
        CarrierDef modifiedCarrierDef = carrierDefService.updateCarrierDef(dto);
        List<CarrierDef> carrierDefList = Collections.singletonList(modifiedCarrierDef);
        return ResponseEntity.ok(new PageImpl<>(carrierDefList,PageRequest.of(0, 1), 1) );
    }

    @Operation(summary = "Carrier Def 복수 삭제", description = "선택된 ID 리스트에 대한 In-Batch 벌크 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteCarrierDefs(@RequestBody List<Long> ids) {
        carrierDefService.deleteCarrierDefs(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}
