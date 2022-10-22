package com.api.gateway.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class LoggingUtils {

  public <T> void logRequest(URI uri, HttpMethod httpMethod, HttpHeaders httpHeaders, T body) {
    String headers = extractHeaders(httpHeaders);
    String requestBody = Optional.ofNullable(body)
        .map(Objects::toString)
        .orElse(Strings.EMPTY);

    log.info(
        "\n---- REQUEST ----\n"
            + "Method: " + httpMethod + "\n"
            + "Uri: " + uri + "\n"
            + "Headers: " + headers + "\n"
            + "Body: " + requestBody + "\n"
    );

  }

  public <T> void logResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, T body) {
    String headers = extractHeaders(httpHeaders);
    String responseBody = Optional.ofNullable(body)
        .map(Objects::toString)
        .orElse(Strings.EMPTY);

    log.info(
        "\n---- RESPONSE ----\n"
            + "Status: " + httpStatus + "\n"
            + "Headers: " + headers + "\n"
            + "Body: " + responseBody + "\n"
    );

  }

  private String extractHeaders(HttpHeaders httpHeaders) {
    return httpHeaders.entrySet()
        .stream()
        .map(header -> header.getKey() + ": " + header.getValue())
        .collect(Collectors.joining("; "));
  }

}
