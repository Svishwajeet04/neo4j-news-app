package news.bharat.the.services;

import news.bharat.the.dtos.JwtResponse;
import news.bharat.the.models.User;

public interface JwtService {
  JwtResponse createJwtResponse(User user);
}
