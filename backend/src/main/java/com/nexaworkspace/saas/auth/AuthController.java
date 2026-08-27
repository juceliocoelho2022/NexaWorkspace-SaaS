package com.nexaworkspace.saas.auth;

import com.nexaworkspace.saas.ratelimit.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    private final RateLimitService rateLimit;

    public AuthController(AuthService service, RateLimitService rateLimit) {
        this.service = service;
        this.rateLimit = rateLimit;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request, HttpServletRequest http) {
        rateLimit.check("register", clientIp(http), 5, Duration.ofMinutes(10));
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request, HttpServletRequest http) {
        rateLimit.check("login", clientIp(http), 10, Duration.ofMinutes(1));
        return service.login(request);
    }

    private String clientIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
