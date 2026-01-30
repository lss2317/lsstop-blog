package com.lsstop.utils;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.lsstop.constant.CommonConst;
import com.lsstop.enums.IllegalPolicyEnum;
import com.lsstop.exception.BusinessException;

import java.util.List;

/**
 * 敏感词工具类
 *
 * @author lishusheng
 * @date 2026/01/30
 */
public class SensitiveWordUtils {

    private SensitiveWordUtils() {
    }

    /**
     * 敏感词处理结果
     *
     * @param hasSensitive 是否包含敏感词
     * @param content      处理后的文本
     */
    public record Result(boolean hasSensitive, String content) {
    }

    /**
     * 检测文本是否包含敏感词
     *
     * @param text 待检测文本
     * @return true-包含敏感词，false-不包含
     */
    public static boolean contains(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return SensitiveWordHelper.contains(text);
    }

    /**
     * 获取文本中的所有敏感词
     *
     * @param text 待检测文本
     * @return 敏感词列表
     */
    public static List<String> findAll(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return SensitiveWordHelper.findAll(text);
    }

    /**
     * 替换文本中的敏感词为 ***
     *
     * @param text 待处理文本
     * @return 替换后的文本
     */
    public static String replace(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return SensitiveWordHelper.replace(text);
    }

    /**
     * 根据策略处理敏感词
     *
     * @param text   待处理文本
     * @param policy 处理策略枚举
     * @return 处理结果（包含是否命中敏感词及处理后文本）
     */
    public static Result process(String text, IllegalPolicyEnum policy) {
        if (text == null || text.isEmpty()) {
            return new Result(false, text);
        }

        boolean hasSensitive = contains(text);
        if (!hasSensitive) {
            return new Result(false, text);
        }

        if (IllegalPolicyEnum.BLOCK == policy) {
            throw new BusinessException(CommonConst.SENSITIVE_WORD_DETECTED);
        }

        if (IllegalPolicyEnum.REPLACE == policy) {
            return new Result(true, replace(text));
        }

        // 策略为审核时，返回原文本，由调用方设置审核状态
        return new Result(true, text);
    }

    /**
     * 校验文本，包含敏感词则抛出异常
     *
     * @param text 待校验文本
     */
    public static void validate(String text) {
        if (contains(text)) {
            throw new BusinessException(CommonConst.SENSITIVE_WORD_DETECTED);
        }
    }
}
