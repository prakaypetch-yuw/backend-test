package com.example.backendtest.service;

import com.example.backendtest.component.UserComponent;
import com.example.backendtest.config.TokenProvider;
import com.example.backendtest.model.entity.Role;
import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.model.response.TokenResponse;
import com.example.backendtest.model.response.UserDetailResponse;
import com.example.backendtest.repository.RoleRepository;
import com.example.backendtest.repository.UserRepository;
import com.example.backendtest.service.impl.UserServiceImpl;
import com.example.backendtest.type.ErrorType;
import com.example.backendtest.type.MemberType;
import com.example.backendtest.type.RoleType;
import com.example.backendtest.utility.ErrorException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcryptEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenProvider jwtTokenUtil;

    @Mock
    private UserComponent userComponent;

    @Test
    public void testValidateRegisterUserRequestShouldErrorWithUserAlreadyExists() throws Exception {
        ErrorException errorException = null;
        Mockito.when(userRepository.findTopByUsernameAndActiveIsTrue(Mockito.anyString())).thenReturn(mockUser());
        try {
            userService.validateRegisterUserRequest(mockRegisterUserRequest());
        } catch (Exception e) {
            Assert.assertEquals(ErrorException.class, e.getClass());
            errorException = (ErrorException) e;
        }
        Assert.assertNotNull(errorException);
        Assert.assertEquals(ErrorType.USER_ALREADY_EXISTS.getCode(), errorException.getCode());
        Assert.assertEquals(ErrorType.USER_ALREADY_EXISTS.getMessage(), errorException.getMessage());
    }

    @Test
    public void testValidateRegisterUserRequestShouldErrorWithInvalidSalary() throws Exception {
        ErrorException errorException = null;
        RegisterUserRequest request = mockRegisterUserRequest();
        request.setSalary(14999);
        try {
            userService.validateRegisterUserRequest(request);
        } catch (Exception e) {
            Assert.assertEquals(ErrorException.class, e.getClass());
            errorException = (ErrorException) e;
        }
        Assert.assertNotNull(errorException);
        Assert.assertEquals(ErrorType.INVALID_SALARY.getCode(), errorException.getCode());
        Assert.assertEquals(ErrorType.INVALID_SALARY.getMessage(), errorException.getMessage());
    }

    @Test
    public void testValidateRegisterUserRequestShouldSuccess() throws Exception {
        ErrorException errorException = null;
        try {
            userService.validateRegisterUserRequest(mockRegisterUserRequest());
        } catch (Exception e) {
            Assert.assertEquals(ErrorException.class, e.getClass());
            errorException = (ErrorException) e;
        }
        Assert.assertNull(errorException);
    }

    @Test
    public void testTransformRegisterUserRequestToUserEntityShouldSuccess() throws Exception {
        DateTimeUtils.setCurrentMillisFixed(1643945881000L);

        Mockito.when(bcryptEncoder.encode(Mockito.anyString())).thenReturn("xxxxxx");
        Mockito.when(roleRepository.findByName(Mockito.anyString())).thenReturn(mockRoleUser());
        User user = userService.transformRegisterUserRequestToUserEntity(mockRegisterUserRequest());
        Assert.assertEquals("testuser", user.getUsername());
        Assert.assertEquals("xxxxxx", user.getPassword());
        Assert.assertEquals("test user", user.getFullName());
        Assert.assertEquals("apartment", user.getAddress());
        Assert.assertEquals("0999999999", user.getPhone());
        Assert.assertEquals("202202049999", user.getReferenceCode());
        Assert.assertEquals(100000, user.getSalary().intValue());
        Assert.assertEquals(MemberType.PLATINUM.getDbValue(), user.getMemberType());
        Assert.assertTrue(user.getActive());
        Assert.assertEquals(1, user.getRoles().size());
        List<Role> roleList = new ArrayList<>(user.getRoles());
        Assert.assertEquals(1, roleList.get(0).getRoleId().intValue());
        Assert.assertEquals(RoleType.USER.getName(), roleList.get(0).getName());
    }

    @Test
    public void testSaveUserShouldSuccess() throws Exception {
        userService.saveUser(mockUser());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testGetUserTokenShouldSuccess() throws Exception {
        Mockito.when(jwtTokenUtil.generateToken(Mockito.any())).thenReturn("tokenx");
        TokenResponse response = userService.getUserToken("username", "password");
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
        Mockito.verify(jwtTokenUtil, Mockito.times(1)).generateToken(Mockito.any());
        Assert.assertNotNull(response);
        Assert.assertEquals("tokenx", response.getToken());
    }

    @Test
    public void testGetUserDetailResponse() throws Exception {
        Mockito.when(userComponent.getUserId()).thenReturn(1L);
        Mockito.when(userRepository.findByUserIdAndActiveIsTrue(Mockito.any())).thenReturn(mockUser());
        UserDetailResponse response = userService.getUserDetail();
        Assert.assertNotNull(response);
        Assert.assertEquals(1L, response.getUserId().longValue());
        Assert.assertEquals("testuser", response.getUsername());
        Assert.assertEquals("test user", response.getFullName());
        Assert.assertEquals("apartment", response.getAddress());
        Assert.assertEquals("0999999999", response.getPhone());
        Assert.assertEquals("202202056777", response.getReferenceCode());
        Assert.assertEquals(20000, response.getSalary().intValue());
        Assert.assertEquals(MemberType.SILVER.getDisplayValue(), response.getMemberType());
        Assert.assertEquals("2022-02-05", response.getCreateDate());
    }

    private RegisterUserRequest mockRegisterUserRequest() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("testuser");
        request.setPassword("123456");
        request.setFullName("test user");
        request.setPhone("0999999999");
        request.setAddress("apartment");
        request.setSalary(100000);
        return request;
    }

    private User mockUser() {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = format.parseDateTime("2022-02-05");
        return User.builder()
                .userId(1L)
                .username("testuser")
                .password("password")
                .fullName("test user")
                .address("apartment")
                .phone("0999999999")
                .referenceCode("202202056777")
                .salary(20000)
                .memberType("silver")
                .active(true)
                .createDate(dateTime.toDate())
                .build();
    }

    private Role mockRoleUser() {
        Role role = new Role();
        role.setRoleId(1);
        role.setName("USER");
        role.setDescription("user role");
        return role;
    }
}
