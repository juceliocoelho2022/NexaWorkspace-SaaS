package com.nexaworkspace.saas.audit;
import com.nexaworkspace.saas.security.SaasPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/audit")
public class AuditController {
 private final AuditLogRepository repo; public AuditController(AuditLogRepository repo){this.repo=repo;}
 @GetMapping @PreAuthorize("hasAnyRole('OWNER','ADMIN')") public List<AuditLog> list(@AuthenticationPrincipal SaasPrincipal p){return repo.findTop20ByTenantIdOrderByCreatedAtDesc(p.tenantId());}
}
