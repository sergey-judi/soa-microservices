package com.api.gateway.service.cache;

import com.api.gateway.model.cache.UserToken;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "userTokenCache")
public class UserTokenCacheService {

  @CachePut(key = "#userId", unless = "#result == null")
  public UserToken put(String userId, UserToken userToken) {
    return userToken;
  }

  @Cacheable(key = "#userId")
  public UserToken get(String userId) {
    return UserToken.create();
  }

}
