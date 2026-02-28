# Redis Serialization Bug (Feb 2026)

## Problem
API trả 500 khi enable Redis Cache.

## Root Cause
Redis sử dụng JDK serialization mặc định.
DTO ProductResponse không tương thích khi deserialize.

## Investigation
- Check log stacktrace
- Debug cache proxy
- Kiểm tra Redis key bằng redis-cli

## Solution
Cấu hình:
- StringRedisSerializer cho key
- GenericJackson2JsonRedisSerializer cho value

## Lesson Learned
Luôn kiểm soát serializer khi dùng Redis.
Không dùng default config trong production.