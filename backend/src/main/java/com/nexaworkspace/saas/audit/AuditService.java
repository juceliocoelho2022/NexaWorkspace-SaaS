package com.nexaworkspace.saas.audit;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;
@Service
public class AuditService {
 private final AuditLogRepository repo; public AuditService(AuditLogRepository repo){this.repo=repo;}
 public void record(UUID tid, UUID uid, String action, String resource, String resourceId){ repo.save(new AuditLog(UUID.randomUUID(),tid,uid,action,resource,resourceId, Instant.now())); }
}
