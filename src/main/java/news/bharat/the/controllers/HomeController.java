package news.bharat.the.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import news.bharat.the.services.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {
  private final NewsService newsService;

  @GetMapping
  public Mono<ResponseEntity<?>> getAllNews(@RequestParam(required = false) String area, @RequestParam(required = false) String userId, Authentication authentication) {
    log.info("authentication : {} ", authentication != null ? authentication.getPrincipal() : null);
    return newsService.getAllNews(area, userId);
  }

}
