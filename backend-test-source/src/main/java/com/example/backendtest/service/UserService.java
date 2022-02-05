package com.example.backendtest.service;


import com.example.backendtest.model.entity.User;
import com.example.backendtest.model.request.RegisterUserRequest;
import com.example.backendtest.model.response.TokenResponse;

public interface UserService {
    void validateRegisterUserRequest(RegisterUserRequest request) throws Exception;

    void saveUser(User user);

    User transformRegisterUserRequestToUserEntity(RegisterUserRequest request);

    TokenResponse getUserToken(String username, String password);
}
