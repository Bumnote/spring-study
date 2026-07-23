package com.laboratory.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

@Slf4j
class CacheTest {

  @Autowired
  CacheManager cacheManager;

  @Test
  void getALlKeyAndValue() {
    for (String cacheName : cacheManager.getCacheNames()) {
      Cache cache = ((CaffeineCache) cacheManager.getCache(cacheName)).getNativeCache();

      for (Object key : cache.asMap().keySet()) {
        Object value = cache.getIfPresent(key);
        log.info("key: {} - value: {}", key, value.toString());
      }
    }

    // or functional
    cacheManager.getCacheNames()
        .stream()
        .map(cacheName -> ((CaffeineCache) cacheManager.getCache(cacheName)).getNativeCache())
        .forEach(cache -> cache.asMap().keySet().forEach(key -> {
          log.info("key: {} - value: {}", key, cache.getIfPresent(key).toString());
        }));
  }


}
