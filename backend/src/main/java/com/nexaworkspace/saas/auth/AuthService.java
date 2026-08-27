package com.nexaworkspace.saas.auth;

import com.nexaworkspace.saas.audit.AuditService;
import com.nexaworkspace.saas.billing.*;
import com.nexaworkspace.saas.common.ApiException;
import com.nexaworkspace.saas.security.JwtService;
import com.nexaworkspace.saas.tenant.*;
import com.nexaworkspace.saas.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
 private final TenantRepository tenants; private final UserRepository users; private final SubscriptionRepository subscriptions; private final PasswordEncoder encoder; private final JwtService jwt; private final AuditService audit;
 public AuthService(TenantRepository tenants,UserRepository users,SubscriptionRepository subscriptions,PasswordEncoder encoder,JwtService jwt,AuditService audit){this.tenants=tenants;this.users=users;this.subscriptions=subscriptions;this.encoder=encoder;this.jwt=jwt;this.audit=audit;}
 @Transactional public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest r){
   if(users.findByEmailIgnoreCase(r.email()).isPresent()) throw new ApiException(HttpStatus.CONFLICT,"E-mail já cadastrado");
   String base=slug(r.companyName()); String slug=base; int i=1; while(tenants.existsBySlug(slug)) slug=base+"-"+(i++);
   var tenant=tenants.save(new Tenant(UUID.randomUUID(),r.companyName().trim(),slug,Instant.now()));
   var user=users.save(new UserAccount(UUID.randomUUID(),tenant,r.name().trim(),r.email().trim().toLowerCase(),encoder.encode(r.password()),Role.OWNER,true,Instant.now()));
   var sub=subscriptions.save(new Subscription(UUID.randomUUID(),tenant,Plan.FREE,"ACTIVE",Instant.now()));
   audit.record(tenant.getId(),user.getId(),"REGISTER","TENANT",tenant.getId().toString());
   return response(user,sub);
 }
 public AuthDtos.AuthResponse login(AuthDtos.LoginRequest r){
   var user=users.findByEmailIgnoreCase(r.email()).orElseThrow(()->new ApiException(HttpStatus.UNAUTHORIZED,"Credenciais inválidas"));
   if(!user.isActive() || !encoder.matches(r.password(),user.getPasswordHash())) throw new ApiException(HttpStatus.UNAUTHORIZED,"Credenciais inválidas");
   var sub=subscriptions.findByTenant_Id(user.getTenant().getId()).orElseThrow(); audit.record(user.getTenant().getId(),user.getId(),"LOGIN","USER",user.getId().toString()); return response(user,sub);
 }
 private AuthDtos.AuthResponse response(UserAccount u, Subscription s){return new AuthDtos.AuthResponse(jwt.issue(u),u.getTenant().getId(),u.getId(),u.getName(),u.getEmail(),u.getRole().name(),s.getPlan().name());}
 private static String slug(String v){ String s=Normalizer.normalize(v,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase().replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)",""); return s.isBlank()?"workspace":s; }
}
