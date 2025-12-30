package com.lsstop.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * <p>
 * 使用 Fastjson2 作为序列化方案
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Configuration
public class RedisConfig {

    /**
     * AutoType 白名单配置，只允许反序列化指定包下的类
     */
    private static final String[] AUTO_TYPE_ACCEPT_LIST = {
            "com.lsstop."
    };

    /**
     * Fastjson2 AutoType 过滤器
     */
    private static final Filter AUTO_TYPE_FILTER = JSONReader.autoTypeFilter(AUTO_TYPE_ACCEPT_LIST);

    /**
     * RedisTemplate 配置
     * Key 使用 String 序列化，Value 使用 Fastjson2 序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 使用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 使用 Fastjson2 序列化
        FastJson2RedisSerializer fastJsonSerializer = new FastJson2RedisSerializer();
        template.setValueSerializer(fastJsonSerializer);
        template.setHashValueSerializer(fastJsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * StringRedisTemplate 配置
     * 专门用于字符串操作
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * Fastjson2 Redis 序列化器
     * <p>
     * 安全特性：
     * 1. 使用白名单机制限制 AutoType，防止反序列化漏洞
     * 2. 写入类名信息确保反序列化正确性
     * 3. 支持复杂对象和泛型类型
     */
    @SuppressWarnings("deprecation")
    public static class FastJson2RedisSerializer implements RedisSerializer<Object> {

        @Override
        public byte[] serialize(Object obj) throws SerializationException {
            if (obj == null) {
                return new byte[0];
            }
            try {
                return JSON.toJSONBytes(obj,
                        JSONWriter.Feature.WriteClassName,
                        JSONWriter.Feature.WriteNulls,
                        JSONWriter.Feature.NotWriteRootClassName,
                        JSONWriter.Feature.WriteMapNullValue
                );
            } catch (Exception e) {
                throw new SerializationException("Fastjson2 序列化失败: " + e.getMessage(), e);
            }
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            try {
                // 使用 autoTypeFilter 配合 JSONReader.Feature 进行反序列化
                return JSON.parseObject(bytes, Object.class, AUTO_TYPE_FILTER,
                        JSONReader.Feature.SupportAutoType
                );
            } catch (Exception e) {
                throw new SerializationException("Fastjson2 反序列化失败: " + e.getMessage(), e);
            }
        }
    }
}