package com.lsstop.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 * <p>
 * 封装 RedisTemplate 常用操作，支持 String、Hash、List、Set、ZSet 五种数据类型
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Component
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否成功
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return 是否成功
     */
    public Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒），-1 表示永不过期，-2 表示键不存在
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断键是否存在
     *
     * @param key 键
     * @return 是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除键
     *
     * @param key 键
     * @return 是否成功
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除键
     *
     * @param keys 键集合
     * @return 删除数量
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 根据前缀删除键
     *
     * @param prefix 键前缀
     */
    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并设置过期时间
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置值并设置过期时间（秒）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取值并转换类型
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : (T) value;
    }

    /**
     * 批量获取值
     *
     * @param keys 键集合
     * @return 值列表（顺序与 keys 一致，不存在的 key 对应位置为 null）
     */
    public List<Object> mGet(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 批量获取值并转换类型
     *
     * @param keys  键集合
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 值列表（顺序与 keys 一致，不存在的 key 对应位置为 null）
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> mGet(Collection<String> keys, Class<T> clazz) {
        List<Object> values = mGet(keys);
        if (values == null) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(values.size());
        for (Object value : values) {
            result.add(value == null ? null : (T) value);
        }
        return result;
    }

    /**
     * 获取 List 类型的值
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   泛型
     * @return List
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : (List<T>) value;
    }

    /**
     * 获取 Set 类型的值
     *
     * @param key   键
     * @param clazz 元素类型
     * @param <T>   泛型
     * @return Set
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getSet(String key, Class<T> clazz) {
        Object value = get(key);
        return value == null ? null : (Set<T>) value;
    }

    /**
     * 获取 Map 类型的值
     *
     * @param key        键
     * @param keyClass   Map 键类型
     * @param valueClass Map 值类型
     * @param <K>        键泛型
     * @param <V>        值泛型
     * @return Map
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass) {
        Object value = get(key);
        return value == null ? null : (Map<K, V>) value;
    }

    /**
     * 如果不存在则设置值
     *
     * @param key   键
     * @param value 值
     * @return 是否设置成功
     */
    public Boolean setIfAbsent(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 如果不存在则设置值（带过期时间）
     *
     * @param key     键
     * @param value   值
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return 是否设置成功
     */
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 增量
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递增 1
     *
     * @param key 键
     * @return 递增后的值
     */
    public Long increment(String key) {
        return increment(key, 1);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 减量
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 递减 1
     *
     * @param key 键
     * @return 递减后的值
     */
    public Long decrement(String key) {
        return decrement(key, 1);
    }

    /**
     * 设置 Hash 字段值
     *
     * @param key   键
     * @param field 字段
     * @param value 值
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 批量设置 Hash 字段值
     *
     * @param key 键
     * @param map 字段-值映射
     */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取 Hash 字段值
     *
     * @param key   键
     * @param field 字段
     * @return 值
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取 Hash 字段值并转换类型
     *
     * @param key   键
     * @param field 字段
     * @param clazz 目标类型
     * @param <T>   泛型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field, Class<T> clazz) {
        Object value = hGet(key, field);
        return value == null ? null : (T) value;
    }

    /**
     * 获取 Hash 所有字段和值
     *
     * @param key 键
     * @return 字段-值映射
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除 Hash 字段
     *
     * @param key    键
     * @param fields 字段列表
     * @return 删除数量
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 判断 Hash 字段是否存在
     *
     * @param key   键
     * @param field 字段
     * @return 是否存在
     */
    public Boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * Hash 字段递增
     *
     * @param key   键
     * @param field 字段
     * @param delta 增量
     * @return 递增后的值
     */
    public Long hIncrement(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取 Hash 大小
     *
     * @param key 键
     * @return 字段数量
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 从左边添加元素
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long lLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从左边批量添加元素
     *
     * @param key    键
     * @param values 值列表
     * @return 列表长度
     */
    public Long lLeftPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从右边添加元素
     *
     * @param key   键
     * @param value 值
     * @return 列表长度
     */
    public Long lRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从右边批量添加元素
     *
     * @param key    键
     * @param values 值列表
     * @return 列表长度
     */
    public Long lRightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 从左边弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从右边弹出元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取列表指定范围的元素
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引（-1 表示到末尾）
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表所有元素
     *
     * @param key 键
     * @return 元素列表
     */
    public List<Object> lGetAll(String key) {
        return lRange(key, 0, -1);
    }

    /**
     * 获取列表指定索引的元素
     *
     * @param key   键
     * @param index 索引
     * @return 元素
     */
    public Object lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取列表长度
     *
     * @param key 键
     * @return 列表长度
     */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 设置列表指定索引的值
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     */
    public void lSet(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 删除列表中指定数量的元素
     *
     * @param key   键
     * @param count 删除数量（0:删除所有, >0:从头删除, <0:从尾删除）
     * @param value 值
     * @return 删除数量
     */
    public Long lRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 添加元素到 Set
     *
     * @param key    键
     * @param values 值列表
     * @return 添加成功的数量
     */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取 Set 所有元素
     *
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断元素是否在 Set 中
     *
     * @param key   键
     * @param value 值
     * @return 是否存在
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取 Set 大小
     *
     * @param key 键
     * @return 元素数量
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 删除 Set 中的元素
     *
     * @param key    键
     * @param values 值列表
     * @return 删除数量
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 随机弹出一个元素
     *
     * @param key 键
     * @return 弹出的元素
     */
    public Object sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 随机获取一个元素（不删除）
     *
     * @param key 键
     * @return 随机元素
     */
    public Object sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 添加元素到 ZSet
     *
     * @param key   键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 批量添加元素到 ZSet
     *
     * @param key    键
     * @param tuples 值-分数集合
     * @return 添加数量
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }

    /**
     * 获取 ZSet 指定范围的元素（按分数升序）
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取 ZSet 指定范围的元素（按分数降序）
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 元素集合
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 根据分数范围获取 ZSet 元素
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<Object> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取元素的分数
     *
     * @param key   键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取元素的排名（升序）
     *
     * @param key   键
     * @param value 值
     * @return 排名（从 0 开始）
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取元素的排名（降序）
     *
     * @param key   键
     * @param value 值
     * @return 排名（从 0 开始）
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 删除 ZSet 中的元素
     *
     * @param key    键
     * @param values 值列表
     * @return 删除数量
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取 ZSet 大小
     *
     * @param key 键
     * @return 元素数量
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 增加元素的分数
     *
     * @param key   键
     * @param value 值
     * @param delta 增量
     * @return 增加后的分数
     */
    public Double zIncrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 统计分数范围内的元素数量
     *
     * @param key 键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素数量
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }
}
