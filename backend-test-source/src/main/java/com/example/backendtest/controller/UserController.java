package com.example.backendtest.controller;

import com.example.backendtest.model.Response;
import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.model.response.TokenResponse;
import com.example.backendtest.model.response.UserDetailResponse;
import com.example.backendtest.service.UserService;
import com.example.backendtest.utility.Constant;
import com.example.backendtest.utility.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(Protocol.API_VERSION_1)
public class UserController {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = Protocol.REGISTER)
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegisterUserRequest request) throws Exception {
        logger.info(Constant.REQUEST_PATTERN, request);
        userService.validateRegisterUserRequest(request);
        User user = userService.transformRegisterUserRequestToUserEntity(request);
        userService.saveUser(user);
        TokenResponse tokenResponse = userService.getUserToken(request.getUsername(), request.getPassword());
        Response<?> response = new Response<>(tokenResponse);
        logger.info(Constant.REQUEST_PATTERN, "register success");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = Protocol.USER)
    public ResponseEntity<?> registerUser() throws Exception {
        logger.info(Constant.REQUEST_PATTERN, "get user detail by token");
        UserDetailResponse userDetailResponse = userService.getUserDetail();
        Response<?> response = new Response<>(userDetailResponse);
        logger.info(Constant.RESPONSE_PATTERN, response);
        return ResponseEntity.ok(response);
    }
}
