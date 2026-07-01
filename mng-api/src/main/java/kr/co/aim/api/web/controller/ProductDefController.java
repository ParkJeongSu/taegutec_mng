package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.service.ProductDefService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.dto.ProductDefSaveRequestDto;
import kr.co.aim.common.dto.ProductDefSearchConditionDto;
import kr.co.aim.domain.model.ProductDef;
import lombok.RequiredArgsConstructor;
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

@Tag(name = "MNG Product Definition", description = "기준정보 Product Def 관리 API")
@RestController
@RequestMapping("/api/v1/mng/product-def")
@RequiredArgsConstructor
@Profile("web")
@ResponseAnnotation
@ConditionalOnProperty(name = "factory.type", havingValue = "powder")
public class ProductDefController {

    private final ProductDefService productDefService;

    @Operation(summary = "Product Def 목록 조회")
    @GetMapping("")
    public ResponseEntity<Page<ProductDef>> getProductDefs(
            ProductDefSearchConditionDto condition,
            @ParameterObject Pageable pageable
    ) {
        Page<ProductDef> result = productDefService.findProductDefWithConditions(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Product Def 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<Page<ProductDef>> getProductDef(@PathVariable("id") Long id) {
        ProductDef result = productDefService.findById(id);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(result), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Product Def 생성")
    @PostMapping("")
    public ResponseEntity<Page<ProductDef>> createProductDef(@RequestBody ProductDefSaveRequestDto dto) {
        ProductDef created = productDefService.createProductDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(created), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Product Def 수정")
    @PutMapping("/{id}")
    public ResponseEntity<Page<ProductDef>> updateProductDef(
            @PathVariable("id") Long id,
            @RequestBody ProductDefSaveRequestDto dto
    ) {
        ProductDef modified = productDefService.updateProductDef(dto);
        return ResponseEntity.ok(new PageImpl<>(Collections.singletonList(modified), PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "Product Def 복수 삭제")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteProductDefs(@RequestBody List<Long> ids) {
        productDefService.deleteProductDefs(ids);
        return ResponseEntity.ok("SUCCESS");
    }
}