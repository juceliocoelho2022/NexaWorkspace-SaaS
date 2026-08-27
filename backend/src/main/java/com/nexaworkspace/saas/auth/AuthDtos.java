package com.nexaworkspace.saas.auth;
import jakarta.validation.constraints.*;
import java.util.UUID;
public final class AuthDtos {
 private AuthDtos(){}
 public record RegisterRequest(@NotBlank String companyName,@NotBlank String name,@Email @NotBlank String email,@Size(min=8) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record AuthResponse(String token, UUID tenantId, UUID userId, String name, String email, String role, String plan){}
}
