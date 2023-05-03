package news.bharat.the.services;

import news.bharat.the.dtos.LoginRequest;
import news.bharat.the.models.User;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<ResponseEntity<Object>> createUser(User user);

  Mono<User> getUserDetails(String id);

  Mono<ResponseEntity<?>> loginUser(LoginRequest request);
}
