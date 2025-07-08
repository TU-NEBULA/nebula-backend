package com.team_nebula.nebula.domain.star.search.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void clearUserCache(Long userId) {
        clearCacheByPattern("autocomplete_service::*:" + userId + ":*");
    }

    private void clearCacheByPattern(String pattern) {
        ScanOptions scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        try (Cursor<byte[]> cursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(scanOptions)) {

            int deleteCount = 0;
            while (cursor.hasNext()) {
                byte[] key = cursor.next();
                redisTemplate.delete(new String(key));
                deleteCount++;
            }
            log.info("Cleared {} keys matching pattern '{}'", deleteCount, pattern);

        } catch (Exception e) {
            log.error("Error clearing Redis cache with pattern {}", pattern, e);
        }
    }
}
