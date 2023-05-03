package news.bharat.the.config.security.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthPrincipal {

  private String userId;

  private String name;

  private String phoneNumber;
}
