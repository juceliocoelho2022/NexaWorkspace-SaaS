package com.nexaworkspace.saas.security;

import com.nexaworkspace.saas.user.Role;
import java.util.UUID;

public record SaasPrincipal(UUID userId, UUID tenantId, String name, String email, Role role) {}
