package com.api.gateway.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  BAD_CREDENTIALS("error.api.unauthorized", "Bad credentials provided", HttpStatus.UNAUTHORIZED),
  NOT_FOUND("error.api.not-found", "Mapping doesn't exist", HttpStatus.NOT_FOUND),
  MISSING_HEADER("error.api.missing-header", "Required header is missing", HttpStatus.BAD_REQUEST),
  API_THROTTLING("error.api.rate-limit-exceeded", "Request rate is limited", HttpStatus.TOO_MANY_REQUESTS),
  INTERNAL("error.api.internal", "Unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;

}
