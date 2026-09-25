package kr.co.aim.api.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.MenuCreateRequestDto;
import kr.co.aim.api.dto.MenuResponse;
import kr.co.aim.api.dto.MenuUpdateRequestDto;
import kr.co.aim.api.dto.UserAuthorizedMenuResponse;
import kr.co.aim.api.service.MenuService;
import kr.co.aim.common.annotation.ResponseAnnotation;
import kr.co.aim.common.condition.MenuSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "MNG Menu", description = "기준정보 메뉴(MENU) 관리 API")
@RestController
@RequestMapping("/api/v1/mng/menu")
@RequiredArgsConstructor
@Slf4j
@Profile("web")
@ResponseAnnotation
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 목록 조회", description = "동적 조건 및 페이징 메뉴 목록 Page 반환")
    @GetMapping("")
    public ResponseEntity<Page<MenuResponse>> getMenus(
            MenuSearchCondition condition,
            @ParameterObject Pageable pageable
    ) {
        Page<MenuResponse> result = menuService.findMenus(condition, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "공장별 메뉴 계층 트리 조회", description = "공장별 메뉴 정렬 계층 목록 Page 반환")
    @GetMapping("/tree")
    public ResponseEntity<Page<MenuResponse>> getTreeMenus(
            @RequestParam(value = "factoryName", required = false) String factoryName
    ) {
        Page<MenuResponse> result = menuService.findTreeMenus(factoryName);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 메뉴 상세 조회", description = "TSID(Long id) 기준 메뉴 상세 정보 Page 반환")
    @GetMapping("/{id}")
    public ResponseEntity<Page<MenuResponse>> getMenu(@PathVariable("id") Long id) {
        MenuResponse result = menuService.findById(id);
        List<MenuResponse> list = new ArrayList<>();
        list.add(result);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "신규 메뉴 등록", description = "신규 메뉴 등록 및 MENU_HISTORY 이력 적재")
    @PostMapping("")
    public ResponseEntity<Page<MenuResponse>> createMenu(@RequestBody MenuCreateRequestDto dto) {
        MenuResponse created = menuService.createMenu(dto);
        List<MenuResponse> list = new ArrayList<>();
        list.add(created);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "메뉴 정보 수정", description = "TSID(Long id) 기준 메뉴 정보 수정 및 동일 TSID 기준 이력 적재")
    @PutMapping("/{id}")
    public ResponseEntity<Page<MenuResponse>> updateMenu(
            @PathVariable("id") Long id,
            @RequestBody MenuUpdateRequestDto dto
    ) {
        MenuResponse updated = menuService.updateMenu(id, dto);
        List<MenuResponse> list = new ArrayList<>();
        list.add(updated);
        return ResponseEntity.ok(new PageImpl<>(list, PageRequest.of(0, 1), 1));
    }

    @Operation(summary = "메뉴 단건 삭제", description = "TSID(Long id) 기준 단건 삭제 및 삭제 이력 적재")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenu(
            @PathVariable("id") Long id,
            @RequestParam(value = "eventUser", required = false, defaultValue = "SYSTEM") String eventUser,
            @RequestParam(value = "eventComment", required = false, defaultValue = "Menu deleted") String eventComment
    ) {
        menuService.deleteMenu(id, eventUser, eventComment);
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "메뉴 복수 벌크 삭제", description = "선택된 TSID(ID) 리스트에 대한 In-Batch 벌크 삭제 및 이력 적재")
    @DeleteMapping("/batch-delete")
    public ResponseEntity<String> deleteMenus(@RequestBody DeleteItemListDto deleteDto) {
        if (deleteDto != null && deleteDto.getIds() != null) {
            menuService.deleteMenus(deleteDto.getIds(), "SYSTEM", "Batch menus deleted");
        }
        return ResponseEntity.ok("SUCCESS");
    }

    @Operation(summary = "사용자 권한 메뉴 계층 트리 조회", description = "사용자 ID(TSID) 기준 권한이 부여된 메뉴 목록을 계층 트리 구조로 반환")
    @GetMapping("/authorized/{userId}")
    public ResponseEntity<List<UserAuthorizedMenuResponse>> getAuthorizedMenuTree(
            @PathVariable("userId") Long userId
    ) {
        List<UserAuthorizedMenuResponse> tree = menuService.findAuthorizedMenuTree(userId);
        return ResponseEntity.ok(tree);
    }
}
