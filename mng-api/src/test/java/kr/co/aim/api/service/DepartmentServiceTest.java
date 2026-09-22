package kr.co.aim.api.service;

import kr.co.aim.api.dto.DepartmentCreateRequestDto;
import kr.co.aim.api.dto.DepartmentResponse;
import kr.co.aim.api.dto.DepartmentUpdateRequestDto;
import kr.co.aim.common.condition.DepartmentSearchCondition;
import kr.co.aim.domain.model.Department;
import kr.co.aim.domain.repository.DepartmentRepository;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import kr.co.aim.infra.persistence.entity.DepartmentHistoryEntity;
import kr.co.aim.infra.persistence.mapper.DepartmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private HistoryService historyService;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private Department sampleDepartment;

    @BeforeEach
    void setUp() {
        sampleDepartment = Department.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .departmentName("생산관리팀")
                .useState("ACTIVE")
                .eventName("DepartmentCreated")
                .eventTime(LocalDateTime.now())
                .eventUser("SYSTEM")
                .eventComment("Initial Department")
                .build();
    }

    @Test
    @DisplayName("부서 등록 성공: TSID 사전 발급 및 히스토리가 함께 적재된다")
    void createDepartment_Success() {
        // given
        DepartmentCreateRequestDto request = DepartmentCreateRequestDto.builder()
                .factoryName("INSERT")
                .departmentName("생산관리팀")
                .useState("ACTIVE")
                .eventName("DepartmentCreated")
                .eventUser("admin")
                .eventComment("신규 부서 생성")
                .build();

        DepartmentHistoryEntity historyEntity = DepartmentHistoryEntity.builder().build();

        when(departmentRepository.findByFactoryNameAndDepartmentName("INSERT", "생산관리팀")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(departmentMapper.toHistoryEntity(any(DepartmentEntity.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(DepartmentHistoryEntity.class))).thenReturn(historyEntity);

        // when
        DepartmentResponse response = departmentService.createDepartment(request);

        // then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("INSERT", response.getFactoryName());
        assertEquals("생산관리팀", response.getDepartmentName());
        assertEquals("ACTIVE", response.getUseState());

        verify(departmentRepository).save(any(Department.class));
        verify(historyService).saveHistory(any(DepartmentHistoryEntity.class));
    }

    @Test
    @DisplayName("부서 등록 실패: 동일 공장에 중복된 부서명이 이미 존재할 경우 예외 발생")
    void createDepartment_DuplicateName_ThrowsException() {
        // given
        DepartmentCreateRequestDto request = DepartmentCreateRequestDto.builder()
                .factoryName("INSERT")
                .departmentName("생산관리팀")
                .build();

        when(departmentRepository.findByFactoryNameAndDepartmentName("INSERT", "생산관리팀")).thenReturn(Optional.of(sampleDepartment));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> departmentService.createDepartment(request));
        verify(departmentRepository, never()).save(any(Department.class));
        verify(historyService, never()).saveHistory(any());
    }

    @Test
    @DisplayName("부서 수정 성공: 정보 변경 후 히스토리가 적재된다")
    void updateDepartment_Success() {
        // given
        Long targetId = sampleDepartment.getId();
        DepartmentUpdateRequestDto updateDto = DepartmentUpdateRequestDto.builder()
                .id(targetId)
                .factoryName("INSERT")
                .departmentName("생산기획팀")
                .useState("ACTIVE")
                .eventName("DepartmentModified")
                .eventUser("operator")
                .eventComment("부서명 수정")
                .build();

        DepartmentHistoryEntity historyEntity = DepartmentHistoryEntity.builder().build();

        when(departmentRepository.findById(targetId)).thenReturn(Optional.of(sampleDepartment));
        when(departmentRepository.findByFactoryNameAndDepartmentName("INSERT", "생산기획팀")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(departmentMapper.toHistoryEntity(any(Department.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(DepartmentHistoryEntity.class))).thenReturn(historyEntity);

        // when
        DepartmentResponse response = departmentService.updateDepartment(targetId, updateDto);

        // then
        assertNotNull(response);
        assertEquals("생산기획팀", response.getDepartmentName());
        verify(departmentRepository).save(any(Department.class));
        verify(historyService).saveHistory(any(DepartmentHistoryEntity.class));
    }

    @Test
    @DisplayName("부서 단건 삭제 성공: 삭제 히스토리가 기록된 후 deleteById가 호출된다")
    void deleteDepartment_Success() {
        // given
        Long targetId = sampleDepartment.getId();
        DepartmentHistoryEntity historyEntity = DepartmentHistoryEntity.builder().build();

        when(departmentRepository.findById(targetId)).thenReturn(Optional.of(sampleDepartment));
        when(departmentMapper.toHistoryEntity(any(DepartmentEntity.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(DepartmentHistoryEntity.class))).thenReturn(historyEntity);

        // when
        departmentService.deleteDepartment(targetId, "admin", "부서 삭제");

        // then
        verify(historyService).saveHistory(any(DepartmentHistoryEntity.class));
        verify(departmentRepository).deleteById(targetId);
    }

    @Test
    @DisplayName("부서 복수 벌크 삭제 성공: 각 대상별 삭제 이력이 남고 deleteAllByIdInBatch가 실행된다")
    void deleteDepartments_BatchSuccess() {
        // given
        List<Long> ids = new ArrayList<>();
        ids.add(101L);
        ids.add(102L);

        Department d1 = Department.builder().id(101L).departmentName("부서1").build();
        Department d2 = Department.builder().id(102L).departmentName("부서2").build();
        DepartmentHistoryEntity historyEntity = DepartmentHistoryEntity.builder().build();

        when(departmentRepository.findById(101L)).thenReturn(Optional.of(d1));
        when(departmentRepository.findById(102L)).thenReturn(Optional.of(d2));
        when(departmentMapper.toHistoryEntity(any(DepartmentEntity.class))).thenReturn(historyEntity);
        when(historyService.saveHistory(any(DepartmentHistoryEntity.class))).thenReturn(historyEntity);

        // when
        departmentService.deleteDepartments(ids, "ADMIN", "다건 부서 삭제");

        // then
        verify(historyService, times(2)).saveHistory(any(DepartmentHistoryEntity.class));
        verify(departmentRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    @DisplayName("부서 조건 목록 조회 성공: Page 객체로 매핑되어 반환된다")
    void findDepartments_Success() {
        // given
        DepartmentSearchCondition condition = new DepartmentSearchCondition();
        condition.setFactoryName("INSERT");

        Pageable pageable = PageRequest.of(0, 20);
        List<Department> list = new ArrayList<>();
        list.add(sampleDepartment);
        Page<Department> page = new PageImpl<>(list, pageable, 1);

        when(departmentRepository.findDepartments(any(), any())).thenReturn(page);

        // when
        Page<DepartmentResponse> result = departmentService.findDepartments(condition, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("생산관리팀", result.getContent().get(0).getDepartmentName());
    }
}
