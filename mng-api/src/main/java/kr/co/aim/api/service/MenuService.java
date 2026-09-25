package kr.co.aim.api.service;

import kr.co.aim.api.dto.MenuCreateRequestDto;
import kr.co.aim.api.dto.MenuResponse;
import kr.co.aim.api.dto.MenuUpdateRequestDto;
import kr.co.aim.api.dto.UserAuthorizedMenuResponse;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.MenuSearchCondition;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.repository.MenuRepository;
import kr.co.aim.infra.persistence.entity.MenuHistoryEntity;
import kr.co.aim.infra.persistence.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final HistoryService historyService;
    private final MenuMapper menuMapper;

    /**
     * 조건 및 페이징 기반 메뉴 목록 조회 (명시적 for 루프 및 if 필터링)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MenuResponse> findMenus(MenuSearchCondition condition, Pageable pageable) {
        List<Menu> allMenus = menuRepository.findAll();
        List<MenuResponse> filteredList = new ArrayList<>();

        for (Menu menu : allMenus) {
            if (menu == null) {
                continue;
            }

            if (condition != null) {
                if (condition.getFactoryName() != null && !condition.getFactoryName().trim().isEmpty()) {
                    if (menu.getFactoryName() == null || !menu.getFactoryName().equalsIgnoreCase(condition.getFactoryName().trim())) {
                        continue;
                    }
                }
                if (condition.getMenuId() != null && !condition.getMenuId().trim().isEmpty()) {
                    if (menu.getMenuId() == null || !menu.getMenuId().toLowerCase().contains(condition.getMenuId().trim().toLowerCase())) {
                        continue;
                    }
                }
                if (condition.getMenuName() != null && !condition.getMenuName().trim().isEmpty()) {
                    if (menu.getMenuName() == null || !menu.getMenuName().toLowerCase().contains(condition.getMenuName().trim().toLowerCase())) {
                        continue;
                    }
                }
                if (condition.getParentId() != null) {
                    if (menu.getParentId() == null || !condition.getParentId().equals(menu.getParentId())) {
                        continue;
                    }
                }
                if (condition.getMenuLevel() != null) {
                    if (menu.getMenuLevel() == null || !condition.getMenuLevel().equals(menu.getMenuLevel())) {
                        continue;
                    }
                }
                if (condition.getIsVisible() != null && !condition.getIsVisible().trim().isEmpty()) {
                    if (menu.getIsVisible() == null || !condition.getIsVisible().trim().equalsIgnoreCase(menu.getIsVisible())) {
                        continue;
                    }
                }
                if (condition.getUseState() != null && !condition.getUseState().trim().isEmpty()) {
                    if (menu.getUseState() == null || !condition.getUseState().trim().equalsIgnoreCase(menu.getUseState())) {
                        continue;
                    }
                }
            }

            filteredList.add(MenuResponse.fromDomain(menu));
        }

        if (pageable == null || pageable.isUnpaged()) {
            return new PageImpl<>(filteredList, PageRequest.of(0, filteredList.isEmpty() ? 1 : filteredList.size()), filteredList.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());

        List<MenuResponse> pageContent = new ArrayList<>();
        if (start <= filteredList.size()) {
            pageContent = filteredList.subList(start, end);
        }

        return new PageImpl<>(pageContent, pageable, filteredList.size());
    }

    /**
     * 공장별 메뉴 계층 트리/정렬 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<MenuResponse> findTreeMenus(String factoryName) {
        List<Menu> menus;
        if (factoryName != null && !factoryName.trim().isEmpty()) {
            menus = menuRepository.findByFactoryNameOrderByMenuLevelAscDisplayOrderAsc(factoryName.trim());
        } else {
            menus = menuRepository.findAll();
        }

        List<MenuResponse> content = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu != null) {
                content.add(MenuResponse.fromDomain(menu));
            }
        }

        int size = content.isEmpty() ? 1 : content.size();
        return new PageImpl<>(content, PageRequest.of(0, size), content.size());
    }

    /**
     * TSID(ID) 기반 메뉴 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public MenuResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 메뉴 ID가 유효하지 않습니다.");
        }
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            throw new IllegalArgumentException("해당 메뉴가 존재하지 않습니다. ID: " + id);
        }
        return MenuResponse.fromDomain(optionalMenu.get());
    }

    /**
     * 신규 메뉴 등록 (CREATE)
     * - 중복 검증 (factoryName + menuName)
     * - TSID 사전 발급
     * - MENU_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MenuResponse createMenu(MenuCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("메뉴 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getMenuId() == null || dto.getMenuId().trim().isEmpty()) {
            throw new IllegalArgumentException("다국어 식별자(menuId)는 필수 입력 항목입니다.");
        }
        if (dto.getMenuName() == null || dto.getMenuName().trim().isEmpty()) {
            throw new IllegalArgumentException("메뉴명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String menuName = dto.getMenuName().trim();

        // 1. 중복 검증
        Optional<Menu> existing = menuRepository.findByFactoryNameAndMenuName(factoryName, menuName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 메뉴명입니다: " + menuName + " (공장: " + factoryName + ")");
        }

        // 2. TSID 사전 채번
        Long id = TsidUtils.nextId();

        // 3. 트랜잭션 메타데이터 생성
        LocalDateTime now = LocalDateTime.now();
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "MenuCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Menu created";

        // 4. 도메인 모델 생성
        Menu menu = Menu.builder()
                .id(id)
                .factoryName(factoryName)
                .menuId(dto.getMenuId().trim())
                .menuName(menuName)
                .parentId(dto.getParentId())
                .menuLevel(dto.getMenuLevel() != null ? dto.getMenuLevel() : 1)
                .filePath(dto.getFilePath())
                .routerPath(dto.getRouterPath())
                .iconName(dto.getIconName())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 10)
                .isVisible(dto.getIsVisible() != null && !dto.getIsVisible().trim().isEmpty() ? dto.getIsVisible().trim() : "Y")
                .useState(dto.getUseState() != null && !dto.getUseState().trim().isEmpty() ? dto.getUseState().trim() : "ACTIVE")
                .eventName(eventName)
                .eventTime(now)
                .eventUser(eventUser)
                .eventComment(eventComment)
                .build();

        // 5. MENU 저장
        Menu savedMenu = menuRepository.save(menu);

        // 6. 동일 트랜잭션 내 MENU_HISTORY 자동 적재
        MenuHistoryEntity historyEntity = menuMapper.toHistoryEntity(savedMenu);
        historyService.saveHistory(historyEntity);

        log.info("Menu created successfully: [id={}, menuId={}, menuName={}, factoryName={}]", savedMenu.getId(), savedMenu.getMenuId(), savedMenu.getMenuName(), savedMenu.getFactoryName());

        return MenuResponse.fromDomain(savedMenu);
    }

    /**
     * TSID(ID) 기반 메뉴 정보 수정 (UPDATE)
     * - 대상 메뉴 존재 검증
     * - 메뉴명 변경 시 중복 검증
     * - MENU_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public MenuResponse updateMenu(Long id, MenuUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 메뉴 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 메뉴 정보가 유효하지 않습니다.");
        }

        // 1. 대상 메뉴 조회
        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 메뉴가 존재하지 않습니다. ID: " + id);
        }

        Menu menu = optionalMenu.get();

        // 2. 메뉴명 또는 공장 변경 시 중복 검증
        if (dto.getMenuName() != null && !dto.getMenuName().trim().isEmpty()) {
            String targetFactory = (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty())
                    ? dto.getFactoryName().trim()
                    : menu.getFactoryName();
            String targetMenuName = dto.getMenuName().trim();

            if (!targetMenuName.equals(menu.getMenuName()) || (targetFactory != null && !targetFactory.equals(menu.getFactoryName()))) {
                Optional<Menu> duplicate = menuRepository.findByFactoryNameAndMenuName(targetFactory, targetMenuName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 메뉴명입니다: " + targetMenuName + " (공장: " + targetFactory + ")");
                }
            }
            menu.setMenuName(targetMenuName);
        }

        if (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty()) {
            menu.setFactoryName(dto.getFactoryName().trim());
        }
        if (dto.getMenuId() != null && !dto.getMenuId().trim().isEmpty()) {
            menu.setMenuId(dto.getMenuId().trim());
        }
        if (dto.getParentId() != null) {
            menu.setParentId(dto.getParentId());
        }
        if (dto.getMenuLevel() != null) {
            menu.setMenuLevel(dto.getMenuLevel());
        }
        if (dto.getFilePath() != null) {
            menu.setFilePath(dto.getFilePath().trim());
        }
        if (dto.getRouterPath() != null) {
            menu.setRouterPath(dto.getRouterPath().trim());
        }
        if (dto.getIconName() != null) {
            menu.setIconName(dto.getIconName().trim());
        }
        if (dto.getDisplayOrder() != null) {
            menu.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getIsVisible() != null && !dto.getIsVisible().trim().isEmpty()) {
            menu.setIsVisible(dto.getIsVisible().trim());
        }
        if (dto.getUseState() != null && !dto.getUseState().trim().isEmpty()) {
            menu.setUseState(dto.getUseState().trim());
        }

        // 3. 이벤트 메타데이터 갱신
        LocalDateTime now = LocalDateTime.now();
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "MenuModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Menu modified";

        menu.setEventName(eventName);
        menu.setEventTime(now);
        menu.setEventUser(eventUser);
        menu.setEventComment(eventComment);

        // 4. MENU 저장
        Menu updatedMenu = menuRepository.save(menu);

        // 5. 동일 트랜잭션 내 MENU_HISTORY 자동 적재
        MenuHistoryEntity historyEntity = menuMapper.toHistoryEntity(updatedMenu);
        historyService.saveHistory(historyEntity);

        log.info("Menu updated successfully: [id={}, menuId={}, menuName={}]", updatedMenu.getId(), updatedMenu.getMenuId(), updatedMenu.getMenuName());

        return MenuResponse.fromDomain(updatedMenu);
    }

    /**
     * TSID(ID) 기반 단건 메뉴 삭제 (DELETE)
     * - 대상 메뉴 조회
     * - MENU_HISTORY에 삭제 이력 적재
     * - MENU에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMenu(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Menu> optionalMenu = menuRepository.findById(id);
        if (optionalMenu.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 메뉴가 존재하지 않습니다. ID: " + id);
        }

        Menu menu = optionalMenu.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        menu.setEventName("MenuDeleted");
        menu.setEventTime(now);
        menu.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        menu.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Menu deleted");

        // 2. 삭제 이력 적재
        MenuHistoryEntity historyEntity = menuMapper.toHistoryEntity(menu);
        historyService.saveHistory(historyEntity);

        // 3. 메뉴 삭제 (TSID 기준)
        menuRepository.deleteById(id);

        log.info("Menu deleted successfully: [id={}, menuName={}]", id, menu.getMenuName());
    }

    /**
     * TSID(ID) 목록 기반 복수 메뉴 벌크 삭제 (DELETE batch)
     * - 각 대상 메뉴에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteMenus(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch menu deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Menu> optionalMenu = menuRepository.findById(id);
            if (optionalMenu.isPresent()) {
                Menu menu = optionalMenu.get();
                menu.setEventName("MenuDeleted");
                menu.setEventTime(now);
                menu.setEventUser(user);
                menu.setEventComment(comment);

                MenuHistoryEntity historyEntity = menuMapper.toHistoryEntity(menu);
                historyService.saveHistory(historyEntity);
            }
        }

        menuRepository.deleteAllByIdInBatch(ids);
        log.info("Menus batch deleted successfully: [count={}]", ids.size());
    }

    /**
     * 사용자 ID(TSID) 기준 권한이 있는 메뉴 목록을 계층 트리 구조로 반환
     * - USER_GROUP_MENU_AUTH 매핑 존재 여부 기준
     * - MENU.USE_STATE = 'ACTIVE', IS_VISIBLE = 'Y'
     * - USER_GROUP.USE_STATE = 'ACTIVE'
     * - 1레벨(대메뉴)의 children에 2레벨(하위메뉴) 트리 구성
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<UserAuthorizedMenuResponse> findAuthorizedMenuTree(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        List<Menu> authorizedMenus = menuRepository.findAuthorizedMenusByUserId(userId);
        if (authorizedMenus == null || authorizedMenus.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserAuthorizedMenuResponse> level1List = new ArrayList<>();
        List<UserAuthorizedMenuResponse> level2List = new ArrayList<>();

        for (Menu menu : authorizedMenus) {
            if (menu == null) {
                continue;
            }
            UserAuthorizedMenuResponse response = UserAuthorizedMenuResponse.fromDomain(menu);
            if (menu.getMenuLevel() != null && menu.getMenuLevel() == 1) {
                level1List.add(response);
            } else {
                level2List.add(response);
            }
        }

        for (UserAuthorizedMenuResponse child : level2List) {
            if (child == null || child.getParentId() == null) {
                continue;
            }
            for (UserAuthorizedMenuResponse parent : level1List) {
                if (parent != null && parent.getId() != null && parent.getId().equals(child.getParentId())) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(child);
                    break;
                }
            }
        }

        return level1List;
    }
}
