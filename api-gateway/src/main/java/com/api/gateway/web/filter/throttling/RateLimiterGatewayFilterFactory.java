package com.api.gateway.web.filter.throttling;

import com.api.gateway.exception.ApiThrottlingException;
import com.api.gateway.exception.MissingRequestHeaderException;
import com.api.gateway.model.cache.UserToken;
import com.api.gateway.properties.ThrottlingProperties;
import com.api.gateway.properties.ThrottlingProperties.UserThrottlingProperties;
import com.api.gateway.service.cache.UserTokenCacheService;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class RateLimiterGatewayFilterFactory
    extends AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

  private static final String DEFAULT_INTERVAL_CONFIG_PARAM = "defaultInterval";
  private static final String REQUEST_RATE_CONFIG_PARAM = "defaultRequestRate";

  private static final String API_USER_HEADER = "Api-User";

  private final ThrottlingProperties throttlingProperties;
  private final UserTokenCacheService userTokenCacheService;

  public RateLimiterGatewayFilterFactory(
      ThrottlingProperties throttlingProperties,
      UserTokenCacheService userTokenCacheService
  ) {
    super(Config.class);
    this.throttlingProperties = throttlingProperties;
    this.userTokenCacheService = userTokenCacheService;
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return List.of(REQUEST_RATE_CONFIG_PARAM, DEFAULT_INTERVAL_CONFIG_PARAM);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String userId = getUserId(exchange);

      int requestRate = getUserSpecificRequestRate(userId)
          .orElse(config.getDefaultRequestRate());

      Duration interval = getUserSpecificInterval(userId)
          .orElse(config.getDefaultInterval());

      UserToken userToken = userTokenCacheService.get(userId);
      log.info("Current user=[{}] token state=[{}]", userId, userToken);

      Instant currentTimestamp = Instant.now();
      Duration elapsedFromLastReset = Duration.between(userToken.getRateResetTimestamp(), currentTimestamp);

      if (elapsedFromLastReset.compareTo(interval) >= 0) {
        log.info("Resetting user=[{}] token due to interval of=[{}] exceeding", userId, interval);
        userToken.setRequestsCount(0);
        userToken.setRateResetTimestamp(currentTimestamp);
      }

      int userRequestsCount = userToken.getRequestsCount();
      assertRequestRateNotExceeded(userId, userRequestsCount, requestRate, interval.minus(elapsedFromLastReset));

      userToken.setRequestsCount(userRequestsCount + 1);
      userTokenCacheService.put(userId, userToken);
      return chain.filter(exchange);
    };
  }

  private void assertRequestRateNotExceeded(String userId, int userRequestsCount, int requestRate, Duration timeout) {
    if (userRequestsCount >= requestRate) {
      throw new ApiThrottlingException(
          String.format(
              "User=[%s] exceeded request rate of [%s] call(-s). Please, try again later in [%s]",
              userId, requestRate, timeout
          )
      );
    }
  }

  private Optional<Integer> getUserSpecificRequestRate(String userId) {
    return Optional.ofNullable(
        throttlingProperties.getUsers().get(userId)
    ).map(UserThrottlingProperties::getRequestRate);
  }

  private Optional<Duration> getUserSpecificInterval(String userId) {
    return Optional.ofNullable(
        throttlingProperties.getUsers().get(userId)
    ).map(UserThrottlingProperties::getInterval);
  }

  private String getUserId(ServerWebExchange exchange) {
    return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(API_USER_HEADER))
        .orElseThrow(
            () -> new MissingRequestHeaderException(
                String.format("Required request header=[%s] is missing", API_USER_HEADER)
            )
        );
  }

  @Value
  public static class Config {
    int defaultRequestRate;
    Duration defaultInterval;
  }

}
