package com.example.backendtest.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailResponse {
    private Long userId;

    private String username;

    private String fullName;

    private String address;

    private String phone;

    private String referenceCode;

    private Integer salary;

    private String memberType;

    private String createDate;
}
