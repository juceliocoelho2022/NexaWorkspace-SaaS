package com.nexaworkspace.saas.billing;

import com.nexaworkspace.saas.audit.AuditService;
import com.nexaworkspace.saas.common.ApiException;
import com.nexaworkspace.saas.security.SaasPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/billing")
public class BillingController {
 private final SubscriptionRepository repo; private final AuditService audit;
 public BillingController(SubscriptionRepository repo,AuditService audit){this.repo=repo;this.audit=audit;}
 @GetMapping public Map<String,Object> current(@AuthenticationPrincipal SaasPrincipal p){var s=repo.findByTenant_Id(p.tenantId()).orElseThrow();return Map.of("plan",s.getPlan(),"status",s.getStatus(),"updatedAt",s.getUpdatedAt());}
 @PatchMapping("/plan") @PreAuthorize("hasAnyRole('OWNER','ADMIN')") public Map<String,Object> change(@AuthenticationPrincipal SaasPrincipal p,@RequestBody Map<String,String> body){
   Plan plan; try{plan=Plan.valueOf(body.getOrDefault("plan","").toUpperCase());}catch(Exception e){throw new ApiException(HttpStatus.BAD_REQUEST,"Plano inválido");}
   var s=repo.findByTenant_Id(p.tenantId()).orElseThrow();s.changePlan(plan);repo.save(s);audit.record(p.tenantId(),p.userId(),"CHANGE_PLAN","SUBSCRIPTION",s.getId().toString());return Map.of("plan",s.getPlan(),"status",s.getStatus(),"note","Billing sandbox: conecte Stripe/Mercado Pago em produção");
 }
}
