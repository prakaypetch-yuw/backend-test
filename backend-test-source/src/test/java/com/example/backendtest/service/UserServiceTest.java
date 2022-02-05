package com.example.backendtest.service;

import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.repository.UserRepository;
import com.example.backendtest.service.impl.UserServiceImpl;
import com.example.backendtest.type.ErrorType;
import com.example.backendtest.type.MemberType;
import com.example.backendtest.utility.ErrorException;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bcryptEncoder;

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
    }

    @Test
    public void testSaveUserShouldSuccess() throws Exception {
        userService.saveUser(mockUser());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
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
        return User.builder()
                .username("testuser")
                .password("password")
                .fullName("test user")
                .address("apartment")
                .phone("0999999999")
                .referenceCode("202202056777")
                .salary(20000)
                .memberType("silver")
                .active(true)
                .build();
    }
}
