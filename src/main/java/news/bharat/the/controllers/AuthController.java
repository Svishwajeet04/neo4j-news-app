package news.bharat.the.controllers;

import jakarta.validation.Valid;
import news.bharat.the.dtos.LoginRequest;
import news.bharat.the.models.User;
import news.bharat.the.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public Mono<ResponseEntity<Object>> createUser(@RequestBody @Valid User user){
    return userService.createUser(user);
  }

  @PostMapping("/login")
  public Mono<ResponseEntity<?>> loginUser(@RequestBody LoginRequest request){
    return userService.loginUser(request);
  }
}
