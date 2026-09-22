package kr.co.aim.api.service;

import kr.co.aim.api.dto.UserGroupMenuAuthBatchSaveRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthCreateRequestDto;
import kr.co.aim.api.dto.UserGroupMenuAuthResponse;
import kr.co.aim.api.dto.UserGroupMenuAuthUpdateRequestDto;
import kr.co.aim.common.condition.UserGroupMenuAuthSearchCondition;
import kr.co.aim.domain.model.UserGroupMenuAuth;
import kr.co.aim.domain.repository.UserGroupMenuAuthRepository;
import kr.co.aim.infra.persistence.entity.PasswordPolicyHistoryEntity;
import kr.co.aim.infra.persistence.entity.UserGroupMenuAuthHistoryEntity;
import kr.co.aim.infra.persistence.mapper.UserGroupMenuAuthMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserGroupMenuAuthServiceTest {

    @Mock
    private UserGroupMenuAuthRepository userGroupMenuAuthRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private UserGroupMenuAuthMapper userGroupMenuAuthMapper;

    @InjectMocks
    private UserGroupMenuAuthService userGroupMenuAuthService;

    private UserGroupMenuAuth sampleAuth;

    @BeforeEach
    void setUp() {
        sampleAuth = UserGroupMenuAuth.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .userGroupId(100L)
                .userGroupName("ADMIN_GROUP")
                .menuId(200L)
                .menuName("사용자 관리")
                .parentId(0L)
                .menuLevel(1)
                .displayOrder(10)
                .filePath("views/system/UserMng.vue")
                .routerPath("/system/user-mng")
                .authSelect("Y")
                .authSave("Y")
                .authDelete("N")
                .eventName("UserGroupMenuAuthCreated")
                .eventTime(LocalDateTime.now())
                .eventUser("SYSTEM")
                .eventComment("Initial menu auth assignment")
                .build();
    }

    @Test
    @DisplayName("메뉴 권한 등록 성공: TSID 사전 발급 및 3-Way JOIN 결과가 함께 반환된다")
    void createUserGroupMenuAuth_Success() {
        // given
        UserGroupMenuAuthCreateRequestDto request = UserGroupMenuAuthCreateRequestDto.builder()
                .factoryName("INSERT")
                .userGroupId(100L)
                .menuId(200L)
                .authSelect("Y")
                .authSave("Y")
                .authDelete("N")
                .eventName("UserGroupMenuAuthCreated")
                .eventUser("admin")
                .eventComment("신규 메뉴 권한 부여")
                .build();

        UserGroupMenuAuthHistoryEntity historyEntity = mock(UserGroupMenuAuthHistoryEntity.class);

        when(userGroupMenuAuthRepository.findByUserGroupIdAndMenuId(100L, 200L)).thenReturn(Optional.empty());
        when(userGroupMenuAuthRepository.save(any(UserGroupMenuAuth.class))).thenAnswer(new Answer<UserGroupMenuAuth>() {
            @Override
            public UserGroupMenuAuth answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(userGroupMenuAuthMapper.toHistoryEntity(any(UserGroupMenuAuth.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(UserGroupMenuAuthHistoryEntity.class))).thenReturn(historyEntity);
        when(userGroupMenuAuthRepository.findById(any())).thenReturn(Optional.of(sampleAuth));

        // when
        UserGroupMenuAuthResponse response = userGroupMenuAuthService.createUserGroupMenuAuth(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("INSERT", response.getFactoryName());
        assertEquals(100L, response.getUserGroupId());
        assertEquals("ADMIN_GROUP", response.getUserGroupName());
        assertEquals(200L, response.getMenuId());
        assertEquals("사용자 관리", response.getMenuName());
        assertEquals("Y", response.getAuthSelect());
        assertEquals("Y", response.getAuthSave());
        assertEquals("N", response.getAuthDelete());

        verify(userGroupMenuAuthRepository).save(any(UserGroupMenuAuth.class));
        verify(historyService).saveHistory(any(UserGroupMenuAuthHistoryEntity.class));
    }

    @Test
    @DisplayName("메뉴 권한 등록 실패: 동일 그룹에 동일 메뉴 권한이 이미 존재할 경우 예외 발생")
    void createUserGroupMenuAuth_Duplicate_ThrowsException() {
        // given
        final UserGroupMenuAuthCreateRequestDto request = UserGroupMenuAuthCreateRequestDto.builder()
                .factoryName("INSERT")
                .userGroupId(100L)
                .menuId(200L)
                .build();

        when(userGroupMenuAuthRepository.findByUserGroupIdAndMenuId(100L, 200L)).thenReturn(Optional.of(sampleAuth));

        // when & then
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userGroupMenuAuthService.createUserGroupMenuAuth(request);
            }
        });
        verify(userGroupMenuAuthRepository, never()).save(any(UserGroupMenuAuth.class));
        verify(historyService, never()).saveHistory(any());
    }

    @Test
    @DisplayName("메뉴 권한 수정 성공: 권한 변경 후 히스토리가 적재된다")
    void updateUserGroupMenuAuth_Success() {
        // given
        Long targetId = sampleAuth.getId();
        UserGroupMenuAuthUpdateRequestDto updateDto = UserGroupMenuAuthUpdateRequestDto.builder()
                .authSelect("Y")
                .authSave("N")
                .authDelete("N")
                .eventName("UserGroupMenuAuthModified")
                .eventUser("operator")
                .eventComment("저장 권한 회수")
                .build();

        UserGroupMenuAuthHistoryEntity historyEntity = mock(UserGroupMenuAuthHistoryEntity.class);

        when(userGroupMenuAuthRepository.findById(targetId)).thenReturn(Optional.of(sampleAuth));
        when(userGroupMenuAuthRepository.save(any(UserGroupMenuAuth.class))).thenAnswer(new Answer<UserGroupMenuAuth>() {
            @Override
            public UserGroupMenuAuth answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(userGroupMenuAuthMapper.toHistoryEntity(any(UserGroupMenuAuth.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(UserGroupMenuAuthHistoryEntity.class))).thenReturn(historyEntity);

        // when
        UserGroupMenuAuthResponse response = userGroupMenuAuthService.updateUserGroupMenuAuth(targetId, updateDto);

        // then
        assertNotNull(response);
        assertEquals("N", response.getAuthSave());
        verify(userGroupMenuAuthRepository).save(any(UserGroupMenuAuth.class));
        verify(historyService).saveHistory(any(UserGroupMenuAuthHistoryEntity.class));
    }

    @Test
    @DisplayName("메뉴 권한 일괄 저장 성공: 여러 메뉴에 대한 권한이 한번에 등록 및 수정된다")
    void saveBatch_Success() {
        // given
        List<UserGroupMenuAuthBatchSaveRequestDto.AuthItem> authItems = new ArrayList<>();
        authItems.add(UserGroupMenuAuthBatchSaveRequestDto.AuthItem.builder()
                .menuId(200L)
                .authSelect("Y")
                .authSave("Y")
                .authDelete("Y")
                .build());

        UserGroupMenuAuthBatchSaveRequestDto batchDto = UserGroupMenuAuthBatchSaveRequestDto.builder()
                .factoryName("INSERT")
                .userGroupId(100L)
                .authList(authItems)
                .eventName("UserGroupMenuAuthBatchSaved")
                .eventUser("admin")
                .eventComment("전체 권한 부여")
                .build();

        List<UserGroupMenuAuth> authList = new ArrayList<>();
        sampleAuth.setAuthDelete("Y");
        authList.add(sampleAuth);

        when(userGroupMenuAuthRepository.findByUserGroupIdAndMenuId(100L, 200L)).thenReturn(Optional.of(sampleAuth));
        when(userGroupMenuAuthRepository.save(any(UserGroupMenuAuth.class))).thenAnswer(new Answer<UserGroupMenuAuth>() {
            @Override
            public UserGroupMenuAuth answer(org.mockito.invocation.InvocationOnMock invocation) {
                return invocation.getArgument(0);
            }
        });
        when(userGroupMenuAuthMapper.toHistoryEntity(any(UserGroupMenuAuth.class))).thenReturn(mock(UserGroupMenuAuthHistoryEntity.class));
        when(historyService.saveHistory(any(UserGroupMenuAuthHistoryEntity.class))).thenReturn(mock(UserGroupMenuAuthHistoryEntity.class));
        when(userGroupMenuAuthRepository.findByUserGroupId(100L)).thenReturn(authList);

        // when
        List<UserGroupMenuAuthResponse> result = userGroupMenuAuthService.saveBatch(batchDto);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Y", result.get(0).getAuthDelete());
        assertEquals("ADMIN_GROUP", result.get(0).getUserGroupName());
        verify(userGroupMenuAuthRepository).save(any(UserGroupMenuAuth.class));
        verify(historyService).saveHistory(any(UserGroupMenuAuthHistoryEntity.class));
    }

    @Test
    @DisplayName("메뉴 권한 단건 삭제 성공: 삭제 히스토리가 기록된 후 deleteById가 호출된다")
    void deleteUserGroupMenuAuth_Success() {
        // given
        Long targetId = sampleAuth.getId();
        UserGroupMenuAuthHistoryEntity historyEntity = mock(UserGroupMenuAuthHistoryEntity.class);

        when(userGroupMenuAuthRepository.findById(targetId)).thenReturn(Optional.of(sampleAuth));
        when(userGroupMenuAuthMapper.toHistoryEntity(any(UserGroupMenuAuth.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(UserGroupMenuAuthHistoryEntity.class))).thenReturn(historyEntity);

        // when
        userGroupMenuAuthService.deleteUserGroupMenuAuth(targetId, "admin", "권한 삭제");

        // then
        verify(historyService).saveHistory(any(UserGroupMenuAuthHistoryEntity.class));
        verify(userGroupMenuAuthRepository).deleteById(targetId);
    }

    @Test
    @DisplayName("메뉴 권한 복수 벌크 삭제 성공: 각 대상별 삭제 이력이 남고 deleteAllByIdInBatch가 실행된다")
    void deleteUserGroupMenuAuths_BatchSuccess() {
        // given
        List<Long> ids = new ArrayList<>();
        ids.add(101L);
        ids.add(102L);

        UserGroupMenuAuth a1 = UserGroupMenuAuth.builder().id(101L).userGroupId(100L).menuId(201L).build();
        UserGroupMenuAuth a2 = UserGroupMenuAuth.builder().id(102L).userGroupId(100L).menuId(202L).build();
        UserGroupMenuAuthHistoryEntity historyEntity = mock(UserGroupMenuAuthHistoryEntity.class);

        when(userGroupMenuAuthRepository.findById(101L)).thenReturn(Optional.of(a1));
        when(userGroupMenuAuthRepository.findById(102L)).thenReturn(Optional.of(a2));
        when(userGroupMenuAuthMapper.toHistoryEntity(any(UserGroupMenuAuth.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(UserGroupMenuAuthHistoryEntity.class))).thenReturn(historyEntity);

        // when
        userGroupMenuAuthService.deleteUserGroupMenuAuths(ids, "ADMIN", "다건 권한 삭제");

        // then
        verify(historyService, times(2)).saveHistory(any(UserGroupMenuAuthHistoryEntity.class));
        verify(userGroupMenuAuthRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    @DisplayName("메뉴 권한 조건 목록 조회 성공: 3-Way JOIN 프로젝션 기반 Page 객체로 매핑되어 반환된다")
    void findUserGroupMenuAuths_Success() {
        // given
        UserGroupMenuAuthSearchCondition condition = new UserGroupMenuAuthSearchCondition();
        condition.setFactoryName("INSERT");
        condition.setUserGroupId(100L);
        condition.setUserGroupName("ADMIN");

        Pageable pageable = PageRequest.of(0, 20);
        List<UserGroupMenuAuth> authList = new ArrayList<>();
        authList.add(sampleAuth);
        Page<UserGroupMenuAuth> page = new PageImpl<>(authList, pageable, 1);

        when(userGroupMenuAuthRepository.findUserGroupMenuAuthsWithDetails(condition, pageable)).thenReturn(page);

        // when
        Page<UserGroupMenuAuthResponse> result = userGroupMenuAuthService.findUserGroupMenuAuths(condition, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("사용자 관리", result.getContent().get(0).getMenuName());
        assertEquals("ADMIN_GROUP", result.getContent().get(0).getUserGroupName());
    }
}
