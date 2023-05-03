package news.bharat.the.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@RestController
public class HeroPageController {

  @GetMapping
  public Mono<Object> loginToView(Principal principal){
    return Mono.just(Map.of("data" , principal));
  }
}
