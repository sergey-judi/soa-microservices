package com.api.gateway.web.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class ErrorResponse {

  String code;
  String message;

}
