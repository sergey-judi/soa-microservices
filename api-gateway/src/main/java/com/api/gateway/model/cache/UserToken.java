package com.api.gateway.model.cache;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class UserToken implements Serializable {

  public static final int INITIAL_REQUESTS_COUNT = 0;

  private int requestsCount;
  private Instant rateResetTimestamp;

  public static UserToken create() {
    return UserToken.builder()
        .requestsCount(INITIAL_REQUESTS_COUNT)
        .rateResetTimestamp(Instant.now())
        .build();
  }

}
