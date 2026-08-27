package com.nexaworkspace.saas.project;
import com.nexaworkspace.saas.security.SaasPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/projects")
public class ProjectController {
 private final ProjectService service; public ProjectController(ProjectService service){this.service=service;}
 @GetMapping public List<ProjectDtos.View> list(@AuthenticationPrincipal SaasPrincipal p){return service.list(p);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public ProjectDtos.View create(@AuthenticationPrincipal SaasPrincipal p,@Valid @RequestBody ProjectDtos.Upsert in){return service.create(p,in);}
 @PutMapping("/{id}") public ProjectDtos.View update(@AuthenticationPrincipal SaasPrincipal p,@PathVariable UUID id,@Valid @RequestBody ProjectDtos.Upsert in){return service.update(p,id,in);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@AuthenticationPrincipal SaasPrincipal p,@PathVariable UUID id){service.delete(p,id);}
}
