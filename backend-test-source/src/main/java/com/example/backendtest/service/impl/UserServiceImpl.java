package com.example.backendtest.service.impl;

import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.repository.UserRepository;
import com.example.backendtest.service.UserService;
import com.example.backendtest.type.ErrorType;
import com.example.backendtest.type.MemberType;
import com.example.backendtest.utility.Constant;
import com.example.backendtest.utility.ErrorException;
import com.example.backendtest.utility.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service(value = "userService")
public class UserServiceImpl implements UserService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bcryptEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bcryptEncoder) {
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    @Override
    public void validateRegisterUserRequest(RegisterUserRequest request) throws Exception {
        User user = userRepository.findTopByUsernameAndActiveIsTrue(request.getUsername());
        if (Objects.nonNull(user)) {
            logger.error(Constant.EXCEPTION_PATTERN, ErrorType.USER_ALREADY_EXISTS.getMessage());
            throw new ErrorException(ErrorType.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (request.getSalary() < 15000) {
            logger.error(Constant.EXCEPTION_PATTERN, ErrorType.INVALID_SALARY.getMessage());
            throw new ErrorException(ErrorType.INVALID_SALARY, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User transformRegisterUserRequestToUserEntity(RegisterUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .password(bcryptEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .referenceCode(Utility.getReferenceCode(request.getPhone()))
                .salary(request.getSalary())
                .memberType(MemberType.findTypeBySalary(request.getSalary()).getDbValue())
                .active(true)
                .build();
    }
}
