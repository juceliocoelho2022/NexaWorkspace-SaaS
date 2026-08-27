package com.nexaworkspace.saas.billing;

import com.nexaworkspace.saas.common.ApiException;
import com.nexaworkspace.saas.security.SaasPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Object> current(@AuthenticationPrincipal SaasPrincipal principal) {
        return service.current(principal);
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public BillingDtos.CheckoutResponse checkout(@AuthenticationPrincipal SaasPrincipal principal,
                                                  @Valid @RequestBody BillingDtos.CheckoutRequest request) {
        return service.checkout(principal, request);
    }

    @PatchMapping("/plan")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public Map<String, Object> change(@AuthenticationPrincipal SaasPrincipal principal,
                                      @RequestBody Map<String, String> body) {
        Plan plan;
        try {
            plan = Plan.valueOf(body.getOrDefault("plan", "").toUpperCase());
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Plano inválido");
        }
        return service.changeSandboxPlan(principal, plan);
    }
}
