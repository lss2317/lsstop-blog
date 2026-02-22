package com.lsstop.service.impl;

import com.lsstop.constant.CommonConst;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.domain.entity.LoginLogEntity;
import com.lsstop.mapper.LoginLogMapper;
import com.lsstop.service.LoginLogService;
import com.lsstop.utils.IpUtils;
import com.lsstop.utils.UserAgentUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 登录日志服务实现类
 *
 * @author lishusheng
 * @date 2026/01/17
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 插入登录日志
     *
     * @param loginLog 登录日志实体
     */
    @Override
    public void insert(LoginLogEntity loginLog) {
        loginLogMapper.insert(loginLog);
    }

    /**
     * 发送登录日志到MQ
     *
     * @param userId    用户ID
     * @param loginType 登录方式
     * @param source    登录来源
     * @param state     登录结果
     * @param message   登录信息
     */
    @Override
    public void sendLoginLog(String userId, Integer loginType, Integer source, Integer state, String message) {
        HttpServletRequest request = getRequest();
        String ipAddress = request != null ? IpUtils.getIpAddress(request) : CommonConst.UNKNOWN;
        String browser = request != null ? UserAgentUtils.getBrowser(request) : CommonConst.UNKNOWN;
        String os = request != null ? UserAgentUtils.getOS(request) : CommonConst.UNKNOWN;

        LoginLogEntity loginLog = LoginLogEntity.builder()
                .userId(userId != null ? userId : CommonConst.UNKNOWN_USER)
                .loginType(loginType)
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .ipRegion(IpUtils.getIpLocation(ipAddress))
                .browser(browser)
                .os(os)
                .type(source)
                .state(state)
                .message(message)
                .build();

        rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.LOGIN_LOG_ROUTING_KEY, loginLog);
    }

    /**
     * 获取当前请求对象
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

}
