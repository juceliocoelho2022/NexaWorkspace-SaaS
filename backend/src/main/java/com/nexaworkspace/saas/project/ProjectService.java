package com.nexaworkspace.saas.project;

import com.nexaworkspace.saas.audit.AuditService;
import com.nexaworkspace.saas.common.ApiException;
import com.nexaworkspace.saas.security.SaasPrincipal;
import com.nexaworkspace.saas.tenant.TenantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
public class ProjectService {
 private final ProjectRepository repo; private final TenantRepository tenants; private final AuditService audit;
 public ProjectService(ProjectRepository repo,TenantRepository tenants,AuditService audit){this.repo=repo;this.tenants=tenants;this.audit=audit;}
 public List<ProjectDtos.View> list(SaasPrincipal p){return repo.findAllByTenant_IdOrderByUpdatedAtDesc(p.tenantId()).stream().map(ProjectService::view).toList();}
 @Transactional public ProjectDtos.View create(SaasPrincipal p,ProjectDtos.Upsert in){var tenant=tenants.getReferenceById(p.tenantId());var now=Instant.now();var x=repo.save(new Project(UUID.randomUUID(),tenant,in.name().trim(),in.description(),in.status()==null?ProjectStatus.PLANNING:in.status(),now,now));audit.record(p.tenantId(),p.userId(),"CREATE","PROJECT",x.getId().toString());return view(x);}
 @Transactional public ProjectDtos.View update(SaasPrincipal p,UUID id,ProjectDtos.Upsert in){var x=repo.findByIdAndTenant_Id(id,p.tenantId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Projeto não encontrado"));x.update(in.name().trim(),in.description(),in.status()==null?x.getStatus():in.status());audit.record(p.tenantId(),p.userId(),"UPDATE","PROJECT",id.toString());return view(x);}
 @Transactional public void delete(SaasPrincipal p,UUID id){var x=repo.findByIdAndTenant_Id(id,p.tenantId()).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"Projeto não encontrado"));repo.delete(x);audit.record(p.tenantId(),p.userId(),"DELETE","PROJECT",id.toString());}
 private static ProjectDtos.View view(Project x){return new ProjectDtos.View(x.getId(),x.getName(),x.getDescription(),x.getStatus(),x.getCreatedAt(),x.getUpdatedAt());}
}
