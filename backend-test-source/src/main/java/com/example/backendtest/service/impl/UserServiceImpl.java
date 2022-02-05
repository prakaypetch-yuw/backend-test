package com.example.backendtest.service.impl;

import com.example.backendtest.component.UserComponent;
import com.example.backendtest.config.TokenProvider;
import com.example.backendtest.model.entity.Role;
import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.model.response.TokenResponse;
import com.example.backendtest.model.response.UserDetailResponse;
import com.example.backendtest.repository.RoleRepository;
import com.example.backendtest.repository.UserRepository;
import com.example.backendtest.service.UserService;
import com.example.backendtest.type.ErrorType;
import com.example.backendtest.type.MemberType;
import com.example.backendtest.type.RoleType;
import com.example.backendtest.utility.Constant;
import com.example.backendtest.utility.ErrorException;
import com.example.backendtest.utility.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bcryptEncoder;

    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;

    private final TokenProvider jwtTokenUtil;

    private final UserComponent userComponent;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bcryptEncoder,
                           RoleRepository roleRepository, @Lazy AuthenticationManager authenticationManager,
                           TokenProvider jwtTokenUtil, UserComponent userComponent) {
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userComponent = userComponent;
    }

    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findTopByUsernameAndActiveIsTrue(username);
        if (Objects.isNull(user)) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User users) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        users.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        return authorities;
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
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleRepository.findByName(RoleType.USER.getName()));

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
                .roles(roleSet)
                .build();
    }

    @Override
    public TokenResponse getUserToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenUtil.generateToken(authentication);
        return new TokenResponse(token);
    }

    @Override
    public UserDetailResponse getUserDetail() throws Exception {
        User user = userRepository.findByUserIdAndActiveIsTrue(userComponent.getUserId());
        if (Objects.isNull(user)) {
            logger.error(Constant.EXCEPTION_PATTERN, ErrorType.USER_NOT_FOUND.getMessage());
            throw new ErrorException(ErrorType.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return UserDetailResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .referenceCode(user.getReferenceCode())
                .salary(user.getSalary())
                .memberType(MemberType.findDbValue(user.getMemberType()).getDisplayValue())
                .createDate(
                        Utility.convertTimeStampToDisplayDateTime(user.getCreateDate().getTime(),
                                Constant.DATE_FORMAT_RESPONSE))
                .build();
    }
}
