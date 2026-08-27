package com.nexaworkspace.saas.security;

import com.nexaworkspace.saas.user.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    public JwtAuthenticationFilter(JwtService jwt){this.jwt=jwt;}
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        var header=req.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer ")){
            try{
                Claims c=jwt.parse(header.substring(7));
                var principal=new SaasPrincipal(UUID.fromString(c.get("uid",String.class)), UUID.fromString(c.get("tid",String.class)), c.get("name",String.class), c.getSubject(), Role.valueOf(c.get("role",String.class)));
                var auth=new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority("ROLE_"+principal.role().name())));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch(Exception ignored){ SecurityContextHolder.clearContext(); }
        }
        chain.doFilter(req,res);
    }
}
