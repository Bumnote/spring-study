package com.laboratory.caffeine.config;

import static com.laboratory.caffeine.config.CacheType.values;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {

    SimpleCacheManager cacheManager = new SimpleCacheManager();

    List<CaffeineCache> caches = Arrays.stream(values())
        .map(cache -> new CaffeineCache(
            cache.getName(),
            Caffeine.newBuilder()
                .expireAfterWrite(cache.getExpireAfterWrite(), TimeUnit.SECONDS)
                .maximumSize(cache.getMaximumSize())
                .build()
        ))
        .collect(Collectors.toList());

    cacheManager.setCaches(caches);
    return cacheManager;
  }
}
