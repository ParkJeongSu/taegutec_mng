package kr.co.aim.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.aim.api.service.AuthService;
import kr.co.aim.api.dto.LoginRequestDto;
import kr.co.aim.api.dto.LoginResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login 호출 시 로그인 성공 응답을 반환한다")
    void loginEndpointSuccess() throws Exception {
        // given
        LoginRequestDto request = LoginRequestDto.builder()
                .factoryName("INSERT")
                .userId("admin")
                .password("password123")
                .build();

        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        roles.add("OPERATOR");

        LoginResponseDto response = LoginResponseDto.builder()
                .accessToken("test-jwt-access-token")
                .refreshToken("test-jwt-refresh-token")
                .tokenType("Bearer")
                .factoryName("INSERT")
                .userId("admin")
                .userName("관리자")
                .departmentName("생산관리팀")
                .roles(roles)
                .build();

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("test-jwt-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("test-jwt-refresh-token"))
                .andExpect(jsonPath("$.data.userId").value("admin"))
                .andExpect(jsonPath("$.data.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.data.roles[1]").value("OPERATOR"));
    }
}
