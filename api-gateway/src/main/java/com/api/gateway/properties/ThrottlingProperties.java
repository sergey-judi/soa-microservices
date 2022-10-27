package com.api.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;

@Data
@Validated
@ConfigurationProperties(prefix = "api.throttling")
public class ThrottlingProperties {

  @NotNull
  private Map<@NotBlank String, @Valid @NotNull UserThrottlingProperties> users;

  @Data
  public static class UserThrottlingProperties {

    @NotNull
    private Integer requestRate;

    @NotNull
    private Duration interval;

  }

}
