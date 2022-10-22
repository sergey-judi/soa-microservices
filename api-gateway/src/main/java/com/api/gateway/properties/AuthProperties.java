package com.api.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
@ConfigurationProperties(prefix = "api.auth")
public class AuthProperties {

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String role;

  @NotBlank
  private String protectedMapping;

}
