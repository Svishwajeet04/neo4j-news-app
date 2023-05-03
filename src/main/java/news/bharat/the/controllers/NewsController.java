package news.bharat.the.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import news.bharat.the.config.security.dtos.AuthPrincipal;
import news.bharat.the.dtos.NewsDto;
import news.bharat.the.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
  private final NewsService newsService;

  @PostMapping
  public Mono<ResponseEntity<?>> addNews(@RequestBody @Valid NewsDto newsDto, Authentication authentication) {
    String userId = ((AuthPrincipal) authentication.getPrincipal()).getUserId();
    return newsService.createNews(newsDto, userId);
  }

  @GetMapping
  public Mono<Object> loginToView(Principal principal){
    return Mono.just(Map.of("data" , principal));
  }
}
