package kr.co.aim.api.service;

import kr.co.aim.api.dto.DepartmentCreateRequestDto;
import kr.co.aim.api.dto.DepartmentResponse;
import kr.co.aim.api.dto.DepartmentUpdateRequestDto;
import kr.co.aim.common.Utils.TsidUtils;
import kr.co.aim.common.condition.DepartmentSearchCondition;
import kr.co.aim.common.enums.UseState;
import kr.co.aim.common.record.TransactionInfo;
import kr.co.aim.domain.command.DepartmentCreateCommand;
import kr.co.aim.domain.command.SysUserCreateCommand;
import kr.co.aim.domain.model.Department;
import kr.co.aim.domain.model.SysUser;
import kr.co.aim.domain.repository.DepartmentRepository;
import kr.co.aim.infra.persistence.entity.DepartmentEntity;
import kr.co.aim.infra.persistence.entity.DepartmentHistoryEntity;
import kr.co.aim.infra.persistence.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final HistoryService historyService;
    private final DepartmentMapper departmentMapper;

    /**
     * 조건 및 페이징 기반 부서 목록 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public Page<DepartmentResponse> findDepartments(DepartmentSearchCondition condition, Pageable pageable) {
        Page<Department> pageResult = departmentRepository.findDepartments(condition, pageable);
        List<DepartmentResponse> content = new ArrayList<>();
        for (Department department : pageResult.getContent()) {
            content.add(DepartmentResponse.fromDomain(department));
        }
        return new PageImpl<>(content, pageable, pageResult.getTotalElements());
    }

    /**
     * TSID(ID) 기반 부서 단건 상세 조회
     */
    @Transactional(value = "mssqlTransactionManager", readOnly = true)
    public DepartmentResponse findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("조회할 부서 ID가 유효하지 않습니다.");
        }
        Optional<Department> optionalDepartment = departmentRepository.findById(id);
        if (optionalDepartment.isEmpty()) {
            throw new IllegalArgumentException("해당 부서가 존재하지 않습니다. ID: " + id);
        }
        return DepartmentResponse.fromDomain(optionalDepartment.get());
    }

    /**
     * 신규 부서 등록 (CREATE)
     * - 중복 검증 (factoryName + departmentName)
     * - TSID 사전 발급
     * - DEPARTMENT_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public DepartmentResponse createDepartment(DepartmentCreateRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("부서 등록 정보가 유효하지 않습니다.");
        }
        if (dto.getFactoryName() == null || dto.getFactoryName().trim().isEmpty()) {
            throw new IllegalArgumentException("공장 구분은 필수 입력 항목입니다.");
        }
        if (dto.getDepartmentName() == null || dto.getDepartmentName().trim().isEmpty()) {
            throw new IllegalArgumentException("부서명은 필수 입력 항목입니다.");
        }

        String factoryName = dto.getFactoryName().trim();
        String departmentName = dto.getDepartmentName().trim();

        // 1. 중복 검증
        Optional<Department> existing = departmentRepository.findByFactoryNameAndDepartmentName(factoryName, departmentName);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + departmentName + " (공장: " + factoryName + ")");
        }

        // 3. 트랜잭션 메타데이터 생성
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "UserCreated";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "User created";

        TransactionInfo tx = TransactionInfo.now(eventName, eventUser, eventComment);

        // 4. 도메인 커맨드 객체 구성 및 엔티티 생성 (TSID 내부 자동 발급)
        DepartmentCreateCommand command = DepartmentCreateCommand.builder()
                .transactionInfo(tx)
                .factoryName(factoryName)
                .departmentName(departmentName)
                .useState(UseState.USE.getValue())
                .build();

        Department department = Department.create(command);

        // 4. DEPARTMENT 저장
        department = departmentRepository.save(department);

        // 5. 동일 트랜잭션 내 DEPARTMENT_HISTORY 자동 적재
        DepartmentHistoryEntity historyEntity = departmentMapper.toHistoryEntity(department);
        historyService.saveHistory(historyEntity);

        log.info("Department created successfully: [id={}, departmentName={}, factoryName={}]", department.getId(), department.getDepartmentName(), department.getFactoryName());

        return DepartmentResponse.fromDomain(department);
    }

    /**
     * TSID(ID) 기반 부서 정보 수정 (UPDATE)
     * - 대상 부서 존재 검증
     * - DEPARTMENT_HISTORY 동일 트랜잭션 내 자동 적재
     */
    @Transactional(value = "mssqlTransactionManager")
    public DepartmentResponse updateDepartment(Long id, DepartmentUpdateRequestDto dto) {
        if (id == null) {
            throw new IllegalArgumentException("수정할 대상 부서 ID가 누락되었습니다.");
        }
        if (dto == null) {
            throw new IllegalArgumentException("수정할 부서 정보가 유효하지 않습니다.");
        }

        // 1. 대상 부서 조회
        Optional<Department> optionalEntity = departmentRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new IllegalArgumentException("수정할 대상 부서가 존재하지 않습니다. ID: " + id);
        }

        Department department = optionalEntity.get();

        // 2. 부서명 변경 시 중복 검증
        if (dto.getDepartmentName() != null && !dto.getDepartmentName().trim().isEmpty()) {
            String targetFactory = (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty())
                    ? dto.getFactoryName().trim()
                    : department.getFactoryName();
            String targetDeptName = dto.getDepartmentName().trim();

            if (!targetDeptName.equals(department.getDepartmentName()) || !targetFactory.equals(department.getFactoryName())) {
                Optional<Department> duplicate = departmentRepository.findByFactoryNameAndDepartmentName(targetFactory, targetDeptName);
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    throw new IllegalArgumentException("이미 존재하는 부서명입니다: " + targetDeptName + " (공장: " + targetFactory + ")");
                }
            }
            department.setDepartmentName(targetDeptName);
        }

        if (dto.getFactoryName() != null && !dto.getFactoryName().trim().isEmpty()) {
            department.setFactoryName(dto.getFactoryName().trim());
        }
        if (dto.getUseState() != null && !dto.getUseState().trim().isEmpty()) {
            department.setUseState(dto.getUseState().trim());
        }

        // 3. 이벤트 메타데이터 갱신
        LocalDateTime now = LocalDateTime.now();
        String eventName = (dto.getEventName() != null && !dto.getEventName().trim().isEmpty()) ? dto.getEventName().trim() : "DepartmentModified";
        String eventUser = (dto.getEventUser() != null && !dto.getEventUser().trim().isEmpty()) ? dto.getEventUser().trim() : "SYSTEM";
        String eventComment = (dto.getEventComment() != null) ? dto.getEventComment().trim() : "Department modified";

        department.setEventName(eventName);
        department.setEventTime(now);
        department.setEventUser(eventUser);
        department.setEventComment(eventComment);

        // 4. DEPARTMENT 저장
        department = departmentRepository.save(department);

        // 5. 동일 트랜잭션 내 DEPARTMENT_HISTORY 자동 적재
        DepartmentHistoryEntity historyEntity = departmentMapper.toHistoryEntity(department);
        historyService.saveHistory(historyEntity);

        log.info("Department updated successfully: [id={}, departmentName={}]", department.getId(), department.getDepartmentName());

        return DepartmentResponse.fromDomain(department);
    }

    /**
     * TSID(ID) 기반 단건 부서 삭제 (DELETE)
     * - 대상 부서 조회
     * - DEPARTMENT_HISTORY에 삭제 이력 적재
     * - DEPARTMENT에서 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteDepartment(Long id, String eventUser, String eventComment) {
        if (id == null) {
            return;
        }

        Optional<Department> optionalEntity = departmentRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new IllegalArgumentException("삭제할 대상 부서가 존재하지 않습니다. ID: " + id);
        }

        Department department = optionalEntity.get();

        // 1. 삭제 이벤트 정보 세팅
        LocalDateTime now = LocalDateTime.now();
        department.setEventName("DepartmentDeleted");
        department.setEventTime(now);
        department.setEventUser((eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM");
        department.setEventComment((eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Department deleted");

        // 2. 삭제 이력 적재
        DepartmentHistoryEntity historyEntity = departmentMapper.toHistoryEntity(department);
        historyService.saveHistory(historyEntity);

        // 3. 부서 삭제 (TSID 기준)
        departmentRepository.deleteById(id);

        log.info("Department deleted successfully: [id={}, departmentName={}]", id, department.getDepartmentName());
    }

    /**
     * TSID(ID) 목록 기반 복수 부서 벌크 삭제 (DELETE batch)
     * - 각 대상 부서에 대한 삭제 이력 자동 적재 (명시적 for 루프 사용)
     * - 배치 일괄 삭제
     */
    @Transactional(value = "mssqlTransactionManager")
    public void deleteDepartments(List<Long> ids, String eventUser, String eventComment) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        String user = (eventUser != null && !eventUser.trim().isEmpty()) ? eventUser.trim() : "SYSTEM";
        String comment = (eventComment != null && !eventComment.trim().isEmpty()) ? eventComment.trim() : "Batch departments deleted";

        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            Optional<Department> optionalDepartment = departmentRepository.findById(id);
            if (optionalDepartment.isPresent()) {
                Department department = optionalDepartment.get();
                department.setEventName("DepartmentDeleted");
                department.setEventTime(now);
                department.setEventUser(user);
                department.setEventComment(comment);

                DepartmentHistoryEntity historyEntity = departmentMapper.toHistoryEntity(department);
                historyService.saveHistory(historyEntity);
            }
        }

        departmentRepository.deleteAllByIdInBatch(ids);
        log.info("Departments batch deleted successfully: [count={}]", ids.size());
    }
}
