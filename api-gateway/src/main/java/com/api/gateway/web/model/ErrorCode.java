package com.api.gateway.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  BAD_CREDENTIALS("error.api.unauthorized", "Bad credentials provided", HttpStatus.UNAUTHORIZED),
  NOT_FOUND("error.api.not-found", "Mapping doesn't exist", HttpStatus.NOT_FOUND),
  INTERNAL("error.api.internal", "Unexpected internal error", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;

}
