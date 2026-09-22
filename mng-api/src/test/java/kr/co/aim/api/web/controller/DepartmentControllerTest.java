package kr.co.aim.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.DepartmentCreateRequestDto;
import kr.co.aim.api.dto.DepartmentResponse;
import kr.co.aim.api.dto.DepartmentUpdateRequestDto;
import kr.co.aim.api.service.DepartmentService;
import kr.co.aim.common.condition.DepartmentSearchCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private DepartmentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = DepartmentResponse.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .departmentName("생산관리팀")
                .useState("ACTIVE")
                .eventName("DepartmentCreated")
                .eventTime(LocalDateTime.of(2026, 9, 22, 9, 0, 0))
                .eventUser("SYSTEM")
                .eventComment("Initial Department")
                .build();
    }

    @Test
    @DisplayName("GET /api/departments: 부서 목록 조회 시 Page 표준 응답 반환")
    void getDepartments_ReturnsPagedStandardResponse() throws Exception {
        // given
        List<DepartmentResponse> content = new ArrayList<>();
        content.add(sampleResponse);
        Page<DepartmentResponse> pageResult = new PageImpl<>(content, PageRequest.of(0, 20), 1);

        when(departmentService.findDepartments(any(), any())).thenReturn(pageResult);

        // when & then
        mockMvc.perform(get("/api/departments")
                        .param("factoryName", "INSERT")
                        .param("departmentName", "생산관리팀")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("정상 처리되었습니다."))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"))
                .andExpect(jsonPath("$.data.content[0].factoryName").value("INSERT"))
                .andExpect(jsonPath("$.data.content[0].departmentName").value("생산관리팀"))
                .andExpect(jsonPath("$.data.content[0].useState").value("ACTIVE"))
                .andExpect(jsonPath("$.data.page.size").value(20))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/departments/{id}: TSID 단건 상세 조회 시에도 Page 기반 표준 응답으로 반환")
    void getDepartmentById_ReturnsPagedStandardResponse() throws Exception {
        // given
        when(departmentService.findById(877810665130787535L)).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(get("/api/departments/{id}", 877810665130787535L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"))
                .andExpect(jsonPath("$.data.content[0].departmentName").value("생산관리팀"))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.size").value(1));
    }

    @Test
    @DisplayName("POST /api/departments: 신규 부서 생성 시 Page 기반 표준 응답으로 반환")
    void createDepartment_ReturnsPagedStandardResponse() throws Exception {
        // given
        DepartmentCreateRequestDto request = DepartmentCreateRequestDto.builder()
                .factoryName("INSERT")
                .departmentName("생산관리팀")
                .useState("ACTIVE")
                .build();

        when(departmentService.createDepartment(any(DepartmentCreateRequestDto.class))).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].departmentName").value("생산관리팀"));
    }

    @Test
    @DisplayName("PUT /api/departments/{id}: TSID 기준 부서 정보 수정 시 Page 기반 표준 응답으로 반환")
    void updateDepartment_ReturnsPagedStandardResponse() throws Exception {
        // given
        DepartmentUpdateRequestDto request = DepartmentUpdateRequestDto.builder()
                .factoryName("INSERT")
                .departmentName("생산기획팀")
                .build();

        when(departmentService.updateDepartment(eq(877810665130787535L), any(DepartmentUpdateRequestDto.class))).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(put("/api/departments/{id}", 877810665130787535L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"));
    }

    @Test
    @DisplayName("DELETE /api/departments/{id}: TSID 기준 단건 삭제 성공")
    void deleteDepartment_Success() throws Exception {
        // given
        doNothing().when(departmentService).deleteDepartment(877810665130787535L, "SYSTEM", "Department deleted");

        // when & then
        mockMvc.perform(delete("/api/departments/{id}", 877810665130787535L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }

    @Test
    @DisplayName("DELETE /api/departments/batch-delete: TSID 목록 기준 복수 벌크 삭제 성공")
    void deleteDepartmentsBatch_Success() throws Exception {
        // given
        DeleteItemListDto request = new DeleteItemListDto();
        List<Long> ids = new ArrayList<>();
        ids.add(100L);
        ids.add(200L);
        request.setIds(ids);

        doNothing().when(departmentService).deleteDepartments(anyList(), anyString(), anyString());

        // when & then
        mockMvc.perform(delete("/api/departments/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}
