package com.example.backendtest.controller;

import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.model.response.TokenResponse;
import com.example.backendtest.model.response.UserDetailResponse;
import com.example.backendtest.service.UserService;
import com.example.backendtest.type.MemberType;
import com.example.backendtest.utility.Protocol;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ControllerErrorHandler())
                .build();
    }

    @Test
    public void testRegisterUserShouldErrorBadRequestCaseRequireFields() throws Exception {
        String url = Protocol.API_VERSION_1 + Protocol.REGISTER;
        RegisterUserRequest request = new RegisterUserRequest();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));

        mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /username is required/i)]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /password is required/i)]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /full name is required/i)]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /phone is required/i)]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /address is required/i)]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /salary is required/i)]").exists());
        Mockito.verify(userService, Mockito.times(0)).validateRegisterUserRequest(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).transformRegisterUserRequestToUserEntity(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).saveUser(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).getUserToken(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRegisterUserShouldErrorBadRequestCaseInvalidPattern() throws Exception {
        String url = Protocol.API_VERSION_1 + Protocol.REGISTER;
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setFullName("test user");
        request.setPhone("099-999-9999");
        request.setAddress("apartment");
        request.setSalary(10000);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));

        mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[?(@.message =~ /phone is invalid/i)]").exists());
        Mockito.verify(userService, Mockito.times(0)).validateRegisterUserRequest(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).transformRegisterUserRequestToUserEntity(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).saveUser(Mockito.any());
        Mockito.verify(userService, Mockito.times(0)).getUserToken(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testRegisterUserShouldSuccess() throws Exception {
        String url = Protocol.API_VERSION_1 + Protocol.REGISTER;
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setFullName("test user");
        request.setPhone("0999999999");
        request.setAddress("apartment");
        request.setSalary(10000);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request));
        Mockito.when(userService.getUserToken(Mockito.anyString(), Mockito.anyString())).thenReturn(new TokenResponse("tokenx"));

        mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token", is("tokenx")));
        Mockito.verify(userService, Mockito.times(1)).validateRegisterUserRequest(Mockito.any());
        Mockito.verify(userService, Mockito.times(1)).transformRegisterUserRequestToUserEntity(Mockito.any());
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any());
        Mockito.verify(userService, Mockito.times(1)).getUserToken(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testGetUserDetailShouldSuccess() throws Exception {
        String url = Protocol.API_VERSION_1 + Protocol.USER;

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(userService.getUserDetail()).thenReturn(mockUserDetailResponse());

        mockMvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username", is("username")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fullName", is("fullname")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.address", is("address")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.phone", is("0999999999")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.referenceCode", is("202202056777")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.salary", is(20000)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.memberType", is(MemberType.SILVER.getDisplayValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createDate", is("2022-02-05")));
        Mockito.verify(userService, Mockito.times(1)).getUserDetail();
    }

    private UserDetailResponse mockUserDetailResponse() {
        return UserDetailResponse.builder()
                .userId(1L)
                .username("username")
                .fullName("fullname")
                .address("address")
                .phone("0999999999")
                .referenceCode("202202056777")
                .salary(20000)
                .memberType(MemberType.SILVER.getDisplayValue())
                .createDate("2022-02-05")
                .build();
    }
}
