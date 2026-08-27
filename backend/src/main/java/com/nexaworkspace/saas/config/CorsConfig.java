package com.nexaworkspace.saas.config;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
@Configuration
public class CorsConfig {
 @Bean CorsConfigurationSource corsConfigurationSource(){
   var c=new CorsConfiguration(); c.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:3000")); c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("*")); c.setAllowCredentials(true);
   var s=new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**",c); return s;
 }
}
