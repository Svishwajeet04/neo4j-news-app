package news.bharat.the.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import news.bharat.the.config.security.dtos.AuthConstants;
import news.bharat.the.dtos.JwtResponse;
import news.bharat.the.models.User;
import news.bharat.the.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtTokenServiceImpl implements JwtService {
  @Autowired
  Algorithm algorithm;

  @Override
  public JwtResponse createJwtResponse(User user) {
    return JwtResponse.builder()
        .jwtToken(generateJwtToken(user))
        .principal(user)
        .build();
  }

  private String generateJwtToken(User user) {
    return JWT.create()
        .withExpiresAt(Instant.now().plus(300, ChronoUnit.MINUTES))
        .withIssuer(AuthConstants.AYODHYA_NEWS)
        .withClaim(AuthConstants.USERID, user.getId())
        .withClaim(AuthConstants.PHONE_NUM, user.getPhoneNumber())
        .withClaim(AuthConstants.NAME, user.getName())
        .sign(algorithm);
  }
}
