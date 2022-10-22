package com.api.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api.auth")
public class AuthProperties {

  private String username;
  private String password;
  private String role;
  private String protectedMapping;

}
