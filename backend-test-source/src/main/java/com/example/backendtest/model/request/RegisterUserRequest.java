package com.example.backendtest.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @ToString.Exclude
    private String password;

    @NotBlank(message = "full name is required")
    private String fullName;

    @NotBlank(message = "phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "phone is invalid")
    private String phone;

    @NotBlank(message = "address is required")
    private String address;

    @NotNull(message = "salary is required")
    private Integer salary;
}
