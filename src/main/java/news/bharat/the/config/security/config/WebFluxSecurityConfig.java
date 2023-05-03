package news.bharat.the.config.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import news.bharat.the.config.security.dtos.AuthConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class WebFluxSecurityConfig {

  private final String secretKey = "something";
  private final ObjectMapper mapper = new ObjectMapper();
  private final ServerAccessDeniedHandler accessDeniedHandler = (exchange, exception) ->
      Mono.defer(() -> Mono.just(exchange.getResponse()))
          .flatMap((response) -> {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
            return response.writeWith(authException(exchange))
                .doOnError((error) -> {
                  DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
                  DataBuffer buffer = dataBufferFactory.wrap(error.getMessage().getBytes(Charset.defaultCharset()));
                  response.writeWith(a -> DataBufferUtils.release(buffer));
                });
          });

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    http
        .exceptionHandling()
        .accessDeniedHandler(this.accessDeniedHandler)
        .and()
        .authorizeExchange()
        .pathMatchers( "/login","/home","/auth/**")
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .addFilterAt(getAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
        .cors().configurationSource(corsConfigurationSource())
        .and()
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//        .formLogin(formLoginSpec -> formLoginSpec.loginPage("/login"))
        /*.oauth2Login(
            oAuth2LoginSpec -> {
              oAuth2LoginSpec.authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/home"));
            }
        )*/;
    return http.build();
  }

  @Bean
  public Algorithm algorithm() {
    return Algorithm.HMAC256(secretKey);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(
        Arrays.asList(
            HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
            HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
            HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
            HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
            HttpHeaders.ORIGIN,
            HttpHeaders.CACHE_CONTROL,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.USER_AGENT,
            HttpHeaders.REFERER));
    configuration.setAllowedMethods(List.of(org.springframework.http.HttpMethod.GET.name(), org.springframework.http.HttpMethod.POST.name()));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  protected AuthenticationWebFilter getAuthenticationWebFilter() {
    ReactiveAuthenticationManager authenticationManager = getAuthenticationManager();
    AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
    filter.setServerAuthenticationConverter(new BearerTokenConverter());
    return filter;
  }

  private ReactiveAuthenticationManager getAuthenticationManager() {
    return new DelegatingReactiveAuthenticationManager(new DBAuthenticationManager(algorithm()));
  }

  public Mono<DataBuffer> authException(ServerWebExchange exchange) {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal)
        .map(token -> (String) token)
        .map(authToken -> {
          String message = "Unauthorized";
          try {
            JWT.require(algorithm())
                .withIssuer(AuthConstants.AYODHYA_NEWS)
                .build().verify(authToken);
          } catch (Exception e) {
            message = e.getMessage();
          }
          return message;
        }).map(ex -> {
          DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
          try {
            return dataBufferFactory.wrap(mapper.writeValueAsBytes(Map.of("error", ex , "status" , "Unauthorized")));
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
  }
}
