package kr.co.aim.api.service;

import kr.co.aim.api.dto.UserGroupMenuAuthBatchSaveRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthCreateRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthResponse;
import kr.co.aim.api.dto.UserGroupMenuAuthUpdateRequestDto;
import kr.co.aim.common.condition.UserGroupMenuAuthSearchCondition;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.UserGroupMenuAuthCreateCommand;
import kr.co.aim.domain.command.UserGroupMenuAuthUpdateCommand;
import kr.co.aim.domain.model.Menu;
import kr.co.aim.domain.model.UserGroupMenuAuth;
import kr.co.aim.domain.repository.MenuRepository;
import kr.co.aim.domain.repository.UserGroupMenuAuthRepository;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthHistoryEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMenuAuthMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupMenuAuthService {

    private final UserGroupMenuAuthRepository userGroupMenuAuthRepository;
    private final MenuRepository menuRepository;
    private final HistoryService historyService;
    private final UserGroupMenuAuthMapper userGroupMenuAuthMapper;

    /**
     * 조건 및 페이징 기반 사용자 그룹별 메뉴 권한 목록 조회 (명시적 for 루프 및 if 필터링)
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<UserGroupMenuAuthResponse> findUserGroupMenuAuths(UserGroupMenuAuthSearchCondition condition, Pageable pageable) {
        List<UserGroupMenuAuth> allAuths = userGroupMenuAuthRepository.findAll();
        List<Menu> allMenus = menuRepository.findAll();

        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu menu : allMenus) {
            if (menu != null && menu.getId() != null) {
                menuMap.put(menu.getId(), menu);
            }
        }

        List<UserGroupMenuAuthResponse> filteredList = new ArrayList<>();

        for (UserGroupMenuAuth auth : allAuths) {
            if (auth == null) {
                continue;
            }

            if (condition != null) {
                if (condition.getFactoryName() != null && !condition.getFactoryName().trim().isEmpty()) {
                    if (auth.getFactoryName() == null || !auth.getFactoryName().equalsIgnoreCase(condition.getFactoryName().trim())) {
                        continue;
                    }
                }
                if (condition.getUserGroupId() != null) {
                    if (auth.getUserGroupId() == null || !condition.getUserGroupId().equals(auth.getUserGroupId())) {
                        continue;
                    }
                }
                if (condition.getMenuId() != null) {
                    if (auth.getMenuId() == null || !condition.getMenuId().equals(auth.getMenuId())) {
                        continue;
                    }
                }
                if (condition.getAuthSelect() != null && !condition.getAuthSelect().trim().isEmpty()) {
                    if (auth.getAuthSelect() == null || !condition.getAuthSelect().trim().equalsIgnoreCase(auth.getAuthSelect())) {
                        continue;
                    }
                }
                if (condition.getAuthSave() != null && !condition.getAuthSave().trim().isEmpty()) {
                    if (auth.getAuthSave() == null || !condition.getAuthSave().trim().equalsIgnoreCase(auth.getAuthSave())) {
                        continue;
                    }
                }
                if (condition.getAuthDelete() != null && !condition.getAuthDelete().trim().isEmpty()) {
                    if (auth.getAuthDelete() == null || !condition.getAuthDelete().trim().equalsIgnoreCase(auth.getAuthDelete())) {
                        continue;
                    }
                }
            }

            Menu menu = null;
            if (auth.getMenuId() != null) {
                menu = menuMap.get(auth.getMenuId());
            }

            filteredList.add(UserGroupMenuAuthResponse.fromDomain(auth, menu));
        }

        if (pageable == null || pageable.isUnpaged()) {
            int size = filteredList.isEmpty() ? 1 : filteredList.size();
            return new PageImpl<>(filteredList, PageRequest.of(0, size), filteredList.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredList.size());

        List<UserGroupMenuAuthResponse> pageContent = new ArrayList<>();
        if (start <= filteredList.size()) {
            pageContent = filteredList.subList(start, end);
        }

        return new PageImpl<>(pageContent, pageable, filteredList.size());
    }

    /**
     * 특정 사용자 그룹의 전체 메뉴 권한 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public List<UserGroupMenuAuthResponse> findByUserGroupId(Long userGroupId) {
        if (userGroupId == null) {
            return new ArrayList<>();
        }

        List<UserGroupMenuAuth> auths = userGroupMenuAuthRepository.findByUserGroupId(userGroupId);
        List<Menu> allMenus = menuRepository.findAll();

        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu menu : allMenus) {
            if (menu != null && menu.getId() != null) {
                menuMap.put(menu.getId(), menu);
            }
        }

        List<UserGroupMenuAuthResponse> result = new ArrayList<>();
        for (UserGroupMenuAuth auth : auths) {
            if (auth != null) {
                Menu menu = null;
                if (auth.getMenuId() != null) {
                    menu = menuMap.get(auth.getMenuId());
                }
                result.add(UserGroupMenuAuthResponse.fromDomain(auth, menu));
            }
        }

        return result;
    }

    /**
     * TSID(ID) 기반 사용자 그룹별 메뉴 권한 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public UserGroupMenuAuthResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 권한 ID가 유효하지 않습니다.");
        }

        Optional<UserGroupMenuAuth> optionalAuth = userGroupMenuAuthRepository.findById(id);
        if (optionalAuth.isEmpty()) {
            throw new IllegalArgumentException("해당 메뉴 권한 설정이 존재하지 않습니다. ID: " + id);
        }

        UserGroupMenuAuth auth = optionalAuth.get();
        Menu menu = null;
        if (auth.getMenuId() != null) {
            Optional<Menu> optionalMenu = menuRepository.findById(auth.getMenuId());
            if (optionalMenu.isPresent()) {
                menu = optionalMenu.get();
            }
        }

        return UserGroupMenuAuthResponse.fromDomain(auth, menu);
    }

    /**
     * 신규 사용자 그룹별 메뉴 권한 등록 (CREATE)
     * - 중복 검증 (userGroupId + menuId)
     * - 도메인 내부 UserGroupMenuAuth.create(command) 호출 (TSID 자동 채번)
     * - USER_GROUP_MENU_AUTH_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupMenuAuthResponse createUserGroupMenuAuth(UserGroupMenuAuthCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("권한 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getUserGroupId() == null) {
            throw new IllegalArgumentException("사용자 그룹 ID는 필수 입력 항목입니다.");
        }
        if (dto.getMenuId() == null) {
            throw new IllegalArgumentException("메뉴 ID는 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        Long userGroupId = dto.getUserGroupId();
        Long menuId = dto.getMenuId();

        // 1. 중복 검증 (동일 그룹에 동일 메뉴 중복 권한 설정 방지)
        Optional<UserGroupMenuAuth> existing = userGroupMenuAuthRepository.findByUserGroupIdAndMenuId(userGroupId, menuId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("해당 사용자 그룹에 이미 등록된 메뉴 권한입니다. (UserGroupId: " + userGroupId + ", MenuId: " + menuId + ")");
        }

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupMenuAuthCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group menu auth created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 도메인 커맨드 생성 및 도메인 모델 생성 (도메인 모델 팩토리 메서드 활용)
        UserGroupMenuAuthCreateCommand command = UserGroupMenuAuthCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .userGroupId(userGroupId)
                .menuId(menuId)
                .authSelect(dto.getAuthSelect() != null ? dto.getAuthSelect().trim() : "N")
                .authSave(dto.getAuthSave() != null ? dto.getAuthSave().trim() : "N")
                .authDelete(dto.getAuthDelete() != null ? dto.getAuthDelete().trim() : "N")
                .build();

        UserGroupMenuAuth auth = UserGroupMenuAuth.create(command);

        // 4. USER_GROUP_MENU_AUTH 저장
        UserGroupMenuAuth savedAuth = userGroupMenuAuthRepository.save(auth);

        // 5. 동일 트랜잭션 내 USER_GROUP_MENU_AUTH_HISTORY 자동 적재
        UserGroupMenuAuthHistoryEntity historyEntity = userGroupMenuAuthMapper.toHistoryEntity(savedAuth);
        historyService.saveHistory(historyEntity);

        log.info("UserGroupMenuAuth created successfully: [id={}, userGroupId={}, menuId={}, select={}, save={}, delete={}]",
                savedAuth.getId(), savedAuth.getUserGroupId(), savedAuth.getMenuId(), savedAuth.getAuthSelect(), savedAuth.getAuthSave(), savedAuth.getAuthDelete());

        Menu menu = null;
        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if (optionalMenu.isPresent()) {
            menu = optionalMenu.get();
        }

        return UserGroupMenuAuthResponse.fromDomain(savedAuth, menu);
    }

    /**
     * TSID(ID) 기반 사용자 그룹별 메뉴 권한 수정 (UPDATE)
     * - 대상 존재 검증
     * - 도메인 내부 auth.update(command) 호출
     * - USER_GROUP_MENU_AUTH_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public UserGroupMenuAuthResponse updateUserGroupMenuAuth(Long id, UserGroupMenuAuthUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 권한 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 권한 정보가 유효하지 않습니다.");
        }

        // 1. 대상 조회
        Optional<UserGroupMenuAuth> optionalAuth = userGroupMenuAuthRepository.findById(id);
        if (optionalAuth.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 메뉴 권한이 존재하지 않습니다. ID: " + id);
        }

        UserGroupMenuAuth auth = optionalAuth.get();

        // 2. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupMenuAuthModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User group menu auth modified";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 3. 도메인 커맨드 생성 및 도메인 모델 수정 (도메인 모델 비즈니스 메서드 활용)
        UserGroupMenuAuthUpdateCommand command = UserGroupMenuAuthUpdateCommand.builder()
                .transactionInfo(tx)
                .authSelect(dto.getAuthSelect() != null ? dto.getAuthSelect().trim() : null)
                .authSave(dto.getAuthSave() != null ? dto.getAuthSave().trim() : null)
                .authDelete(dto.getAuthDelete() != null ? dto.getAuthDelete().trim() : null)
                .build();

        auth.update(command);

        // 4. USER_GROUP_MENU_AUTH 저장
        UserGroupMenuAuth updatedAuth = userGroupMenuAuthRepository.save(auth);

        // 5. 동일 트랜잭션 내 USER_GROUP_MENU_AUTH_HISTORY 자동 적재
        UserGroupMenuAuthHistoryEntity historyEntity = userGroupMenuAuthMapper.toHistoryEntity(updatedAuth);
        historyService.saveHistory(historyEntity);

        log.info("UserGroupMenuAuth updated successfully: [id={}, userGroupId={}, menuId={}, select={}, save={}, delete={}]",
                updatedAuth.getId(), updatedAuth.getUserGroupId(), updatedAuth.getMenuId(), updatedAuth.getAuthSelect(), updatedAuth.getAuthSave(), updatedAuth.getAuthDelete());

        Menu menu = null;
        if (updatedAuth.getMenuId() != null) {
            Optional<Menu> optionalMenu = menuRepository.findById(updatedAuth.getMenuId());
            if (optionalMenu.isPresent()) {
                menu = optionalMenu.get();
            }
        }

        return UserGroupMenuAuthResponse.fromDomain(updatedAuth, menu);
    }

    /**
     * 사용자 그룹별 메뉴 권한 일괄 저장 (BATCH SAVE/UPDATE)
     * - MES 권한 매트릭스 화면에서 그룹 단위로 여러 메뉴 권한을 한 번에 등록/수정
     * - 기존 매핑이 존재하면 update, 없으면 create 수행
     */
    @Transactional(value = "mssqlTransactionManager")
    public List<UserGroupMenuAuthResponse> saveBatch(UserGroupMenuAuthBatchSaveRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("일괄 저장 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getUserGroupId() == null) {
            throw new IllegalArgumentException("사용자 그룹 ID는 필수 입력 항목입니다.");
        }
        if (dto.getAuthList() == null || dto.getAuthList().isEmpty()) {
            return new ArrayList<>();
        }

        String factoryName = dto.getFactoryName().trim();
        Long userGroupId = dto.getUserGroupId();

        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserGroupMenuAuthBatchSaved";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Batch menu auth saved";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        List<Menu> allMenus = menuRepository.findAll();
        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu m : allMenus) {
            if (m != null && m.getId() != null) {
                menuMap.put(m.getId(), m);
            }
        }

        List<UserGroupMenuAuthResponse> results = new ArrayList<>();

        for (UserGroupMenuAuthBatchSaveRequestDto.AuthItem item : dto.getAuthList()) {
            if (item == null || item.getMenuId() == null) {
                continue;
            }

            Long menuId = item.getMenuId();
            String authSelect = item.getAuthSelect() != null ? item.getAuthSelect().trim() : "N";
            String authSave = item.getAuthSave() != null ? item.getAuthSave().trim() : "N";
            String authDelete = item.getAuthDelete() != null ? item.getAuthDelete().trim() : "N";

            Optional<UserGroupMenuAuth> existing = userGroupMenuAuthRepository.findByUserGroupIdAndMenuId(userGroupId, menuId);

            UserGroupMenuAuth targetAuth;
            if (existing.isPresent()) {
                targetAuth = existing.get();
                UserGroupMenuAuthUpdateCommand updateCommand = UserGroupMenuAuthUpdateCommand.builder()
                        .transactionInfo(tx)
                        .authSelect(authSelect)
                        .authSave(authSave)
                        .authDelete(authDelete)
                        .build();
                targetAuth.update(updateCommand);
            } else {
                UserGroupMenuAuthCreateCommand createCommand = UserGroupMenuAuthCreateCommand.builder()
                        .transactionInfo(tx)
                        .factoryName(factoryName)
                        .userGroupId(userGroupId)
                        .menuId(menuId)
                        .authSelect(authSelect)
                        .authSave(authSave)
                        .authDelete(authDelete)
                        .build();
                targetAuth = UserGroupMenuAuth.create(createCommand);
            }

            UserGroupMenuAuth saved = userGroupMenuAuthRepository.save(targetAuth);

            UserGroupMenuAuthHistoryEntity historyEntity = userGroupMenuAuthMapper.toHistoryEntity(saved);
            historyService.saveHistory(historyEntity);

            Menu menu = menuMap.get(menuId);
            results.add(UserGroupMenuAuthResponse.fromDomain(saved, menu));
        }

        log.info("Batch UserGroupMenuAuth saved successfully: [userGroupId={}, count={}]", userGroupId, results.size());

        return results;
    }

    /**
     * TSID(ID) 기반 단건 사용자 그룹별 메뉴 권한 삭제 (DELETE)
     * - 대상 조회
     * - USER_GROUP_MENU_AUTH_HISTORY에 삭제 이력 적재
     * - USER_GROUP_MENU_AUTH에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroupMenuAuth(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<UserGroupMenuAuth> optionalAuth = userGroupMenuAuthRepository.findById(id);
        if (optionalAuth.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 메뉴 권한이 존재하지 않습니다. ID: " + id);
        }

        UserGroupMenuAuth auth = optionalAuth.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        auth.setEventName("UserGroupMenuAuthDeleted");
        auth.setEventTime(now);
        auth.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        auth.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "User group menu auth deleted");

        // 2. 삭제 이력 적재
        UserGroupMenuAuthHistoryEntity historyEntity = userGroupMenuAuthMapper.toHistoryEntity(auth);
        historyService.saveHistory(historyEntity);

        // 3. 메뉴 권한 삭제 (TSID 기준)
        userGroupMenuAuthRepository.deleteById(id);

        log.info("UserGroupMenuAuth deleted successfully: [id={}, userGroupId={}, menuId={}]", id, auth.getUserGroupId(), auth.getMenuId());
    }

    /**
     * TSID(ID) 목록 기반 복수 메뉴 권한 벌크 삭제 (DELETE batch)
     * - 각 대상에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteUserGroupMenuAuths(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch user group menu auth deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<UserGroupMenuAuth> optionalAuth = userGroupMenuAuthRepository.findById(id);
            if (optionalAuth.isPresent()) {
                UserGroupMenuAuth auth = optionalAuth.get();
                auth.setEventName("UserGroupMenuAuthDeleted");
                auth.setEventTime(now);
                auth.setEventUser(user);
                auth.setEventComment(comment);

                UserGroupMenuAuthHistoryEntity historyEntity = userGroupMenuAuthMapper.toHistoryEntity(auth);
                historyService.saveHistory(historyEntity);
            }
        }

        userGroupMenuAuthRepository.deleteAllByIdInBatch(ids);
        log.info("UserGroupMenuAuths batch deleted successfully: [count={}]", ids.size());
    }
}
