package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.strategy.FactoryGALInterfaceStrategy;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.GALDetailInterfaceSearchCondition;
import kr.co.aim.common.condition.GALInterfaceSearchCondition;
import kr.co.aim.common.condition.GALPartSearchCondition;
import kr.co.aim.domain.model.GALDetailInterfaceResponse;
import kr.co.aim.domain.model.GALInterfaceResponse;
import kr.co.aim.domain.model.GALPartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MNG GAL Interface", description = "MNG Interface 관련 API")
@RestController
@RequestMapping("/api/v1/mng/interface")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class GALInterfaceController {
    private final FactoryGALInterfaceStrategy factoryGALInterfaceStrategy;

    @Operation(summary = "GAL Interface List", description = "GAL Interface 조회")
    @GetMapping("")
    public ResponseEntity<Page<GALInterfaceResponse>> getInterfaceList(
            @org.springdoc.core.annotations.ParameterObject
            GALInterfaceSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<GALInterfaceResponse> reuslt = factoryGALInterfaceStrategy.getInterfaceList(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

    @Operation(summary = "GAL Detail List", description = "GAL Detail 조회")
    @GetMapping("/detail")
    public ResponseEntity<Page<GALDetailInterfaceResponse>> getInterfaceList(
            GALDetailInterfaceSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<GALDetailInterfaceResponse> reuslt = factoryGALInterfaceStrategy.getDetailInterfaceList(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

    @Operation(summary = "GAL Part List", description = "GAL Part 조회")
    @GetMapping("/part")
    public ResponseEntity<Page<GALPartResponse>> getPartList(
            @org.springdoc.core.annotations.ParameterObject
            GALPartSearchCondition condition,
            @org.springdoc.core.annotations.ParameterObject
            Pageable pageable
    ) {
        Page<GALPartResponse> reuslt = factoryGALInterfaceStrategy.getPartList(condition,pageable);
        return ResponseEntity.ok(reuslt);
    }

}
