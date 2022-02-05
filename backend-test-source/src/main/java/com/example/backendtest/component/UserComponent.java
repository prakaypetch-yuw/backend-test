package com.example.backendtest.component;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Data
@Component
@Scope(
        value = "request",
        proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class UserComponent {
    private Long userId;

    private String fullName;
}
