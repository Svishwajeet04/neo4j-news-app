package news.bharat.the.config.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import news.bharat.the.config.security.dtos.AuthConstants;
import news.bharat.the.config.security.dtos.AuthPrincipal;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class DBAuthenticationManager implements ReactiveAuthenticationManager {

  private Algorithm algorithm;

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    return authenticationAdapter(authentication);
  }

  private Mono<Authentication> authenticationAdapter(Authentication authentication) {
    String authToken = authentication.getPrincipal().toString();
    return verifyJWTAndGetAuthenticationToken(authToken);
  }

  private Mono<Authentication> verifyJWTAndGetAuthenticationToken(String token) {
    return Mono.justOrEmpty(token).map((authToken) -> {
      DecodedJWT decodedJWT =
          JWT.require(algorithm).withIssuer(AuthConstants.AYODHYA_NEWS).build().verify(authToken);
      String phoneNumber = decodedJWT.getClaim(AuthConstants.PHONE_NUM).asString();
      String name = decodedJWT.getClaim(AuthConstants.NAME).asString();
      String userId = decodedJWT.getClaim(AuthConstants.USERID).asString();
      AuthPrincipal principal = buildCurrentUserAuthPrincipal(userId, phoneNumber, name);
      return (Authentication) new PreAuthenticatedAuthenticationToken(principal, authToken, AuthorityUtils.NO_AUTHORITIES);
    }).onErrorResume(a -> Mono.just(new PreAuthenticatedAuthenticationToken(token, null)));
  }

  // TODO   make db call here to add roles present for the user...
  private AuthPrincipal buildCurrentUserAuthPrincipal(
      String userId, String phoneNumber, String name) {
    return AuthPrincipal.builder()
        .userId(userId)
        .phoneNumber(phoneNumber)
        .name(name)
        .build();
  }
}