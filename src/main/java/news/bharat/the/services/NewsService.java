package news.bharat.the.services;

import news.bharat.the.dtos.NewsDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface NewsService {
  Mono<ResponseEntity<?>> createNews(NewsDto newsDto, String userId);

  Mono<ResponseEntity<?>> getAllNews(String area, String userId);
}
