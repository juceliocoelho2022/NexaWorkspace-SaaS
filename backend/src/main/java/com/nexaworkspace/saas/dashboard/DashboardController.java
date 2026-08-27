package com.nexaworkspace.saas.dashboard;
import com.nexaworkspace.saas.billing.SubscriptionRepository;
import com.nexaworkspace.saas.project.*;
import com.nexaworkspace.saas.security.SaasPrincipal;
import com.nexaworkspace.saas.user.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/dashboard")
public class DashboardController {
 private final ProjectRepository projects; private final UserRepository users; private final SubscriptionRepository subs;
 public DashboardController(ProjectRepository projects,UserRepository users,SubscriptionRepository subs){this.projects=projects;this.users=users;this.subs=subs;}
 @GetMapping public Map<String,Object> get(@AuthenticationPrincipal SaasPrincipal p){var s=subs.findByTenant_Id(p.tenantId()).orElseThrow();return Map.of("projects",projects.countByTenant_Id(p.tenantId()),"activeProjects",projects.countByTenant_IdAndStatus(p.tenantId(),ProjectStatus.ACTIVE),"members",users.countByTenant_Id(p.tenantId()),"plan",s.getPlan(),"role",p.role(),"userName",p.name());}
}
