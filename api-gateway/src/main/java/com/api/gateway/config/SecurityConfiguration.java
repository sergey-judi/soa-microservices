package com.api.gateway.config;

import com.api.gateway.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AuthProperties authProperties;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return http.httpBasic(customizer -> customizer.authenticationEntryPoint(new CustomizedAuthenticationEntryPoint()))
        .authorizeExchange().pathMatchers(authProperties.getProtectedMapping()).authenticated() // protect configured mapping
        .and().authorizeExchange().anyExchange().permitAll() // disable authentication for other routes
        .and().csrf().disable()
        .build();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService() {
    return new MapReactiveUserDetailsService(getEnvironmentalUser());
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private UserDetails getEnvironmentalUser() {
    return User.withUsername(authProperties.getUsername())
        .password(passwordEncoder().encode(authProperties.getPassword()))
        .roles(authProperties.getRole())
        .build();
  }

}
