package com.lsstop.aop;

import com.lsstop.annotation.AccessLimit;
import com.lsstop.constant.RedisConst;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;

/**
 * 接口限流切面
 * <p>
 * 基于 Redis + Lua 脚本实现固定窗口限流，保证原子性
 *
 * @author lishusheng
 * @date 2026/03/28
 */
@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class AccessLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lua 脚本：原子性 INCR + EXPIRE
     * 返回当前计数，若首次访问则设置过期时间
     */
    private static final String LUA_SCRIPT = """
            local key = KEYS[1]
            local expire = tonumber(ARGV[1])
            local current = redis.call('INCR', key)
            if current == 1 then
                redis.call('EXPIRE', key, expire)
            end
            return current
            """;

    private static final DefaultRedisScript<Long> REDIS_SCRIPT;

    static {
        REDIS_SCRIPT = new DefaultRedisScript<>();
        REDIS_SCRIPT.setScriptText(LUA_SCRIPT);
        REDIS_SCRIPT.setResultType(Long.class);
    }

    @Around("@annotation(accessLimit)")
    public Object around(ProceedingJoinPoint joinPoint, AccessLimit accessLimit) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        String key = buildKey(request);
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();

        if (seconds <= 0 || maxCount <= 0) {
            log.warn("限流参数无效 - URI: {}, seconds: {}, maxCount: {}", request.getRequestURI(), seconds, maxCount);
            return joinPoint.proceed();
        }

        Long count;
        try {
            count = stringRedisTemplate.execute(
                    REDIS_SCRIPT,
                    Collections.singletonList(key),
                    String.valueOf(seconds)
            );
        } catch (Exception e) {
            log.error("限流 Redis 执行异常，放行请求 - URI: {}", request.getRequestURI(), e);
            return joinPoint.proceed();
        }

        if (count != null && count > maxCount) {
            log.warn("触发限流 - IP: {}, URI: {}, 当前次数: {}, 限制: {}/{}s",
                    IpUtils.getIpAddress(request), request.getRequestURI(), count, maxCount, seconds);
            throw new BusinessException(StatusEnum.REQUEST_FREQUENTLY, accessLimit.msg());
        }

        return joinPoint.proceed();
    }

    /**
     * 构建限流 Key：rate_limit:{IP}:{URI}
     */
    private String buildKey(HttpServletRequest request) {
        String ip = IpUtils.getIpAddress(request);
        String uri = request.getRequestURI();
        return RedisConst.RATE_LIMIT + ip + ":" + uri;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
