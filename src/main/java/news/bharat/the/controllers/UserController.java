package news.bharat.the.controllers;

import lombok.extern.slf4j.Slf4j;
import news.bharat.the.config.security.dtos.AuthPrincipal;
import news.bharat.the.models.User;
import news.bharat.the.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping
  public Mono<User> getUserDetails(Authentication authentication){
    String userId  = ((AuthPrincipal)authentication.getPrincipal()).getUserId();
    return userService.getUserDetails(userId);
  }
}
