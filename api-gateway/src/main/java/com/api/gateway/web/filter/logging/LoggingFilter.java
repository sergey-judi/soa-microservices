package com.api.gateway.web.filter.logging;

import com.api.gateway.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoggingFilter implements WebFilter {

  private static final AntPathMatcher pathMatcher = new AntPathMatcher();
  private final AuthProperties authProperties;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String requestPath = exchange.getRequest().getURI().getPath();

    // skip logging for unprotected mappings
    if (!pathMatcher.match(authProperties.getProtectedMapping(), requestPath)) {
      return chain.filter(exchange);
    }

    ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
      @Override
      public ServerHttpRequest getRequest() {
        return new RequestLoggingInterceptor(super.getRequest());
      }

      @Override
      public ServerHttpResponse getResponse() {
        return new ResponseLoggingInterceptor(super.getResponse());
      }
    };

    return chain.filter(exchangeDecorator);
  }

}
