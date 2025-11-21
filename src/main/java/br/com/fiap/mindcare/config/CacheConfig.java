package br.com.fiap.mindcare.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Cache simples usando ConcurrentMapCacheManager
}
