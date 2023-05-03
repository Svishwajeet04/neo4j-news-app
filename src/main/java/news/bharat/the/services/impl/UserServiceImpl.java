package news.bharat.the.services.impl;

import lombok.extern.slf4j.Slf4j;
import news.bharat.the.dtos.LoginRequest;
import news.bharat.the.models.User;
import news.bharat.the.repositories.UserRepository;
import news.bharat.the.services.JwtService;
import news.bharat.the.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  PasswordEncoder passwordEncoder;
  @Override
  public Mono<ResponseEntity<Object>> createUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user)
          .map(u-> ResponseEntity.status(HttpStatus.CREATED).body((Object) u))
          .onErrorResume(e ->
              Mono.just(ResponseEntity.badRequest().body(Map.of("message", e.getCause().getMessage())))
          );
  }

  @Override
  public Mono<User> getUserDetails(String id) {
    return userRepository.findById(Mono.just(id));
  }

  @Override
  public Mono<ResponseEntity<?>> loginUser(LoginRequest request) {
    return userRepository.findByPhoneNumber(request.phoneNumber())
        .switchIfEmpty(Mono.just(new User()))
        .flatMap(user -> {
          if(passwordEncoder.matches(request.password() , user.getPassword())){
            return Mono.defer(() -> Mono.just(ResponseEntity.ok(jwtService.createJwtResponse(user))));
          }else{
            return Mono.defer(() -> Mono.just(ResponseEntity.badRequest().body(Map.of("message" , "problem logging you in"))));
          }
        });
  }
}
