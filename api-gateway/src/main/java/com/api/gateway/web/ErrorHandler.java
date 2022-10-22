package com.api.gateway.web;

import com.api.gateway.web.model.ErrorCode;
import com.api.gateway.web.model.ErrorResponse;
import com.api.gateway.util.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
public class ErrorHandler implements WebExceptionHandler {

  private final List<HttpMessageWriter<?>> messageWriters;

  private final List<ViewResolver> viewResolvers;

  public ErrorHandler(ServerCodecConfigurer serverCodecConfigurer,
                      ObjectProvider<List<ViewResolver>> viewResolversProvider) {
    this.messageWriters = serverCodecConfigurer.getWriters();
    this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    ServerHttpRequest request = exchange.getRequest();
    if (ex instanceof BadCredentialsException) {
      log.error("Handling bad credentials exception [{}]", ex.getMessage());
      LoggingUtils.logRequest(request.getURI(), request.getMethod(), request.getHeaders(), "");
      return sendErrorResponse(exchange, ErrorCode.BAD_CREDENTIALS);
    }

    if (ex instanceof ResponseStatusException) {
      log.error("Handling missing mapping exception [{}]", ex.getMessage());
      LoggingUtils.logRequest(request.getURI(), request.getMethod(), request.getHeaders(), "");
      return sendErrorResponse(exchange, ErrorCode.NOT_FOUND);
    }

    log.error("Handling internal exception [{}]", ex.getMessage());
    return sendErrorResponse(exchange, ErrorCode.INTERNAL);
  }

  private ErrorResponse buildErrorResponse(ErrorCode errorCode) {
    return ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());
  }

  private Mono<Void> sendErrorResponse(ServerWebExchange exchange, ErrorCode errorCode) {
    ErrorResponse body = buildErrorResponse(errorCode);

    LoggingUtils.logResponse(errorCode.getStatus(), exchange.getResponse().getHeaders(), body);

    return ServerResponse.status(errorCode.getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(body))
        .flatMap(response -> response.writeTo(exchange, new ErrorResponseContext()));
  }

  private class ErrorResponseContext implements ServerResponse.Context {

    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
      return ErrorHandler.this.messageWriters;
    }

    @Override
    public List<ViewResolver> viewResolvers() {
      return ErrorHandler.this.viewResolvers;
    }
  }

}
