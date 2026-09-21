package kr.co.aim.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.dto.DeleteItemListDto;
import kr.co.aim.api.dto.SysUserCreateRequestDto;
import kr.co.aim.api.dto.SysUserResponseDto;
import kr.co.aim.api.dto.SysUserUpdateRequestDto;
import kr.co.aim.api.service.SysUserService;
import kr.co.aim.common.condition.SysUserSearchCondition;
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

@WebMvcTest(controllers = SysUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SysUserService sysUserService;

    private SysUserResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = SysUserResponseDto.builder()
                .id(877810665130787535L)
                .factoryName("INSERT")
                .userId("admin")
                .userName("관리자")
                .departmentId(1L)
                .email("admin@taegutec.co.kr")
                .phoneNumber("010-1234-5678")
                .userState("ACTIVE")
                .failedLoginCount(0)
                .eventName("UserCreated")
                .eventTime(LocalDateTime.of(2026, 9, 21, 13, 0, 0))
                .eventUser("SYSTEM")
                .eventComment("Initial Admin User")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/mng/users: 사용자 목록 조회 시 통일된 Page 표준 응답 반환")
    void getUsers_ReturnsPagedStandardResponse() throws Exception {
        // given
        List<SysUserResponseDto> content = new ArrayList<>();
        content.add(sampleResponse);
        Page<SysUserResponseDto> pageResult = new PageImpl<>(content, PageRequest.of(0, 20), 1);

        when(sysUserService.findUsers(any(SysUserSearchCondition.class), any(Pageable.class))).thenReturn(pageResult);

        // when & then
        mockMvc.perform(get("/api/v1/mng/users")
                        .param("factoryName", "INSERT")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("정상 처리되었습니다."))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"))
                .andExpect(jsonPath("$.data.content[0].factoryName").value("INSERT"))
                .andExpect(jsonPath("$.data.content[0].userId").value("admin"))
                .andExpect(jsonPath("$.data.content[0].userName").value("관리자"))
                .andExpect(jsonPath("$.data.content[0].email").value("admin@taegutec.co.kr"))
                .andExpect(jsonPath("$.data.content[0].phoneNumber").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.content[0].userState").value("ACTIVE"))
                .andExpect(jsonPath("$.data.page.size").value(20))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.totalPages").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/mng/users/{id}: 단건 상세 조회 시에도 Page 기반 표준 응답으로 반환")
    void getUserById_ReturnsPagedStandardResponse() throws Exception {
        // given
        when(sysUserService.findById(877810665130787535L)).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(get("/api/v1/mng/users/{id}", 877810665130787535L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"))
                .andExpect(jsonPath("$.data.content[0].userId").value("admin"))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.size").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/mng/users: 신규 사용자 생성 시 Page 기반 표준 응답으로 반환")
    void createUser_ReturnsPagedStandardResponse() throws Exception {
        // given
        SysUserCreateRequestDto request = SysUserCreateRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("plain1234")
                .userName("관리자")
                .build();

        when(sysUserService.createUser(any(SysUserCreateRequestDto.class))).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(post("/api/v1/mng/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].userId").value("admin"));
    }

    @Test
    @DisplayName("PUT /api/v1/mng/users/{id}: 사용자 정보 수정 시 Page 기반 표준 응답으로 반환")
    void updateUser_ReturnsPagedStandardResponse() throws Exception {
        // given
        SysUserUpdateRequestDto request = SysUserUpdateRequestDto.builder()
                .factoryName("INSERT")
                .userName("수정관리자")
                .build();

        when(sysUserService.updateUser(eq(877810665130787535L), any(SysUserUpdateRequestDto.class))).thenReturn(sampleResponse);

        // when & then
        mockMvc.perform(put("/api/v1/mng/users/{id}", 877810665130787535L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value("877810665130787535"));
    }

    @Test
    @DisplayName("DELETE /api/v1/mng/users/{id}: 단건 삭제 성공")
    void deleteUser_Success() throws Exception {
        // given
        doNothing().when(sysUserService).deleteUser(877810665130787535L, "SYSTEM", "User deleted");

        // when & then
        mockMvc.perform(delete("/api/v1/mng/users/{id}", 877810665130787535L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }

    @Test
    @DisplayName("DELETE /api/v1/mng/users/batch-delete: 복수 벌크 삭제 성공")
    void deleteUsersBatch_Success() throws Exception {
        // given
        DeleteItemListDto request = new DeleteItemListDto();
        List<Long> ids = new ArrayList<>();
        ids.add(100L);
        ids.add(200L);
        request.setIds(ids);

        doNothing().when(sysUserService).deleteUsers(anyList(), anyString(), anyString());

        // when & then
        mockMvc.perform(delete("/api/v1/mng/users/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value("SUCCESS"));
    }
}
