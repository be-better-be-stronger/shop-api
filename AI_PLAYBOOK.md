# AI_PLAYBOOK

## Project context
- Stack: Spring Boot 3, Java 17, MySQL, Flyway, Redis, JWT
- Package convention: com.shop/{config,security,auth,product,category,cart,order,common}
- Style: API responses standardized, global exception handler, logging, validation
- Goal: production-style backend, not demo

## AI rules I follow
1) I provide context + constraints + output format
2) I request a plan before code when feature is non-trivial
3) I ask AI to list assumptions and risks
4) I require tests for core logic
5) I always do a quick security + edge-case check