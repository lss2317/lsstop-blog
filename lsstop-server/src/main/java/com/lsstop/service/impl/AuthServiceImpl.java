package com.lsstop.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.lsstop.config.BlogConfig;
import com.lsstop.config.JwtConfig;
import com.lsstop.config.OAuthConfig;
import com.lsstop.constant.AuthConst;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.*;
import com.lsstop.domain.entity.UserAuthEntity;
import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;
import com.lsstop.enums.*;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.UserMapper;
import com.lsstop.service.AuthService;
import com.lsstop.service.LoginLogService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthMapper authMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private BlogConfig blogConfig;

    @Resource
    private OAuthConfig oauthConfig;

    @Resource
    private WebsiteConfigService websiteConfigService;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 邮箱密码登录
     *
     * @param dto    邮箱登录参数
     * @param source 登录来源（前台/后台）
     * @return 用户信息
     */
    @Override
    public LoginVO emailLogin(EmailLoginDTO dto, LoginSourceEnum source) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String password = dto.getPassword();
        String userId = null;

        try {
            // 查询用户认证信息
            UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
            if (userAuth == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.ACCOUNT_OR_PASSWORD_ERROR);
            }
            userId = userAuth.getUserId();

            // 验证密码
            if (!PasswordUtils.verify(password, userAuth.getCredential())) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.ACCOUNT_OR_PASSWORD_ERROR);
            }

            // 检查用户是否被禁用
            UserEntity user = userMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(StatusEnum.BLACK_LIST_ERROR, AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            userMapper.updateLastLoginTime(userId);

            // 发送登录成功日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), source.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGIN.getCode(), email, AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth, source);
        } catch (BusinessException e) {
            // 发送登录失败日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), email, e.getMessage());
            throw e;
        }
    }

    /**
     * 邮箱验证码登录
     *
     * @param dto    验证码登录参数
     * @param source 登录来源（前台/后台）
     * @return 用户信息
     */
    @Override
    public LoginVO emailCodeLogin(EmailCodeLoginDTO dto, LoginSourceEnum source) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String code = dto.getCode().trim();
        String codeKey = RedisConst.EMAIL_CODE + CodePurposeEnum.LOGIN.getKey() + ":" + email;
        String userId = null;

        try {
            // 验证验证码
            String storedCode = redisUtils.get(codeKey, String.class);
            if (storedCode == null || !storedCode.equals(code)) {
                throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
            }

            // 查询用户认证信息
            UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
            if (userAuth == null) {
                throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
            }
            userId = userAuth.getUserId();

            // 检查用户是否被禁用
            UserEntity user = userMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(StatusEnum.BLACK_LIST_ERROR, AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            userMapper.updateLastLoginTime(userId);

            // 登录成功后删除验证码
            redisUtils.delete(codeKey);

            // 发送登录成功日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), source.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGIN.getCode(), email, AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth, source);
        } catch (BusinessException e) {
            // 发送登录失败日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), email, e.getMessage());
            throw e;
        }
    }

    /**
     * QQ登录
     *
     * @param dto    QQ登录参数
     * @param source 登录来源（前台/后台）
     * @return 用户信息
     */
    @Override
    public LoginVO qqLogin(QQLoginDTO dto, LoginSourceEnum source) {
        String userId = null;
        String openId = null;

        try {
            // 用 code 换取 access_token
            String tokenUrl = String.format(AuthConst.QQ_ACCESS_TOKEN_URL,
                    oauthConfig.getQq().getAppId(),
                    oauthConfig.getQq().getAppKey(),
                    dto.getCode(),
                    URLEncoder.encode(oauthConfig.getQq().getRedirectUri(), StandardCharsets.UTF_8));
            String tokenResponse = restTemplate.getForObject(tokenUrl, String.class);

            if (tokenResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }
            JSONObject tokenJson = JSONObject.parseObject(tokenResponse);
            String accessToken = tokenJson.getString(AuthConst.QQ_RESPONSE_ACCESS_TOKEN);
            if (accessToken == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }

            // 用 access_token 获取 openId
            String openIdUrl = String.format(AuthConst.QQ_OPENID_URL, accessToken);
            String openIdResponse = restTemplate.getForObject(openIdUrl, String.class);
            if (openIdResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }
            JSONObject openIdJson = JSONObject.parseObject(openIdResponse);
            openId = openIdJson.getString(AuthConst.QQ_RESPONSE_OPENID);
            if (openId == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }

            // 根据 openId 查询用户认证信息
            UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(openId, LoginTypeEnum.QQ.getCode());
            if (userAuth == null) {
                throw new BusinessException(AuthConst.QQ_NOT_BINDDED);
            }
            userId = userAuth.getUserId();

            // 检查用户是否被禁用
            UserEntity user = userMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(StatusEnum.BLACK_LIST_ERROR, AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            userMapper.updateLastLoginTime(userId);

            // 发送登录成功日志
            loginLogService.sendLoginLog(userId, LoginTypeEnum.QQ.getCode(), source.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGIN.getCode(), openId, AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth, source);
        } catch (BusinessException e) {
            loginLogService.sendLoginLog(userId, LoginTypeEnum.QQ.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), openId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("QQ登录失败", e);
            loginLogService.sendLoginLog(userId, LoginTypeEnum.QQ.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), openId, AuthConst.QQ_LOGIN_FAILED);
            throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_LOGIN_FAILED);
        }
    }

    /**
     * 微博登录
     *
     * @param dto    微博登录参数
     * @param source 登录来源（前台/后台）
     * @return 用户信息
     */
    @Override
    public LoginVO weiboLogin(WeiboLoginDTO dto, LoginSourceEnum source) {
        String userId = null;
        String uid = null;

        try {
            // 用 code 换取 access_token 和 uid（使用 POST body 发送参数）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", oauthConfig.getWeibo().getAppKey());
            params.add("client_secret", oauthConfig.getWeibo().getAppSecret());
            params.add("grant_type", "authorization_code");
            params.add("code", dto.getCode());
            params.add("redirect_uri", oauthConfig.getWeibo().getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            String tokenResponse = restTemplate.postForObject(
                    AuthConst.WEIBO_ACCESS_TOKEN_URL, request, String.class);

            if (tokenResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.WEIBO_AUTH_FAILED);
            }
            JSONObject tokenJson = JSONObject.parseObject(tokenResponse);
            String accessToken = tokenJson.getString(AuthConst.WEIBO_RESPONSE_ACCESS_TOKEN);
            Object uidObj = tokenJson.get(AuthConst.WEIBO_RESPONSE_UID);
            if (accessToken == null || uidObj == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.WEIBO_AUTH_FAILED);
            }
            uid = String.valueOf(uidObj);

            // 根据 uid 查询用户认证信息
            UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(uid, LoginTypeEnum.WEIBO.getCode());
            if (userAuth == null) {
                throw new BusinessException(AuthConst.WEIBO_NOT_BINDDED);
            }
            userId = userAuth.getUserId();

            // 检查用户是否被禁用
            UserEntity user = userMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(StatusEnum.BLACK_LIST_ERROR, AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            userMapper.updateLastLoginTime(userId);

            // 发送登录成功日志
            loginLogService.sendLoginLog(userId, LoginTypeEnum.WEIBO.getCode(), source.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGIN.getCode(), uid, AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth, source);
        } catch (BusinessException e) {
            loginLogService.sendLoginLog(userId, LoginTypeEnum.WEIBO.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), uid, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("微博登录失败", e);
            loginLogService.sendLoginLog(userId, LoginTypeEnum.WEIBO.getCode(), source.getCode(), LoginResultEnum.FAIL.getCode(), AuthActionEnum.LOGIN.getCode(), uid, AuthConst.WEIBO_LOGIN_FAILED);
            throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.WEIBO_LOGIN_FAILED);
        }
    }

    /**
     * 构建登录返回结果
     *
     * @param userAuth 用户认证信息
     * @param source   登录来源（决定生成前台或后台token）
     * @return 登录结果
     */
    private LoginVO buildLoginVO(UserAuthEntity userAuth, LoginSourceEnum source) {
        String userId = userAuth.getUserId();

        // 查询用户资料
        UserProfileEntity userProfileEntity = userMapper.selectProfileById(userId);
        LoginVO loginVO = userProfileEntity.asViewObject(LoginVO.class);

        // 根据来源生成不同有效期的Token（内部自动生成sessionId）
        JwtUtils.TokenPair tokenPair;
        String sessionId;
        String redisKey;
        Long refreshTokenExpiration;

        if (source == LoginSourceEnum.ADMIN) {
            tokenPair = jwtUtils.generateAdminTokenPair(userId);
            sessionId = jwtUtils.getSessionId(tokenPair.getRefreshToken());
            redisKey = RedisConst.ADMIN_REFRESH_TOKEN + userId + ":" + sessionId;
            refreshTokenExpiration = jwtConfig.getAdmin().getRefreshTokenExpiration();
        } else {
            tokenPair = jwtUtils.generateFrontTokenPair(userId);
            sessionId = jwtUtils.getSessionId(tokenPair.getRefreshToken());
            redisKey = RedisConst.FRONT_REFRESH_TOKEN + userId + ":" + sessionId;
            refreshTokenExpiration = jwtConfig.getFront().getRefreshTokenExpiration();
        }

        loginVO.setAccessToken(tokenPair.getAccessToken());
        loginVO.setRefreshToken(tokenPair.getRefreshToken());

        // 存储refreshToken到Redis（设置与JWT相同的过期时间）
        redisUtils.set(redisKey, tokenPair.getRefreshToken(), refreshTokenExpiration, TimeUnit.MILLISECONDS);

        // 清除用户主页缓存（因登录后IP归属地可能变化）
        redisUtils.delete(RedisConst.USER_HOME_ME + userId);
        redisUtils.delete(RedisConst.USER_HOME_PUBLIC + userId);

        return loginVO;
    }

    /**
     * 用户登出
     * <p>根据refreshToken解析用户ID，清除Redis中存储的refreshToken</p>
     * <p>宽松处理：即使token过期或无效，也不报错，确保用户能正常退出</p>
     *
     * @param refreshToken 刷新令牌
     * @param source       登录来源（前台/后台）
     */
    @Override
    public void logout(String refreshToken, LoginSourceEnum source) {
        try {
            String userId = jwtUtils.getSubjectIgnoreExpiration(refreshToken);
            String sessionId = jwtUtils.getSessionIdIgnoreExpiration(refreshToken);
            if (userId != null && sessionId != null) {
                String redisKey = (source == LoginSourceEnum.ADMIN)
                        ? RedisConst.ADMIN_REFRESH_TOKEN + userId + ":" + sessionId
                        : RedisConst.FRONT_REFRESH_TOKEN + userId + ":" + sessionId;
                // 校验传入的token与Redis中存储的是否一致，防止旧token踢下线
                String storedToken = redisUtils.get(redisKey, String.class);
                if (storedToken != null && storedToken.equals(refreshToken)) {
                    redisUtils.delete(redisKey);
                    // 发送退出登录日志到MQ
                    loginLogService.sendLoginLog(userId, null, source.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGOUT.getCode(), null, AuthConst.LOGOUT_SUCCESS);
                }
            }
        } catch (Exception e) {
            // 静默处理，退出登录不应因为token解析失败而报错
        }
    }

    /**
     * 刷新token
     * <p>验证refreshToken有效性，生成新的accessToken和refreshToken</p>
     *
     * @param refreshToken 刷新token
     * @param source       登录来源（前台/后台）
     * @return 新的token信息
     */
    @Override
    public TokenVO refreshToken(String refreshToken, LoginSourceEnum source) {
        // 验证refreshToken
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BusinessException(StatusEnum.NOT_LOGIN, AuthConst.REFRESH_TOKEN_INVALID);
        }
        // 获取用户ID和会话ID
        String userId = jwtUtils.getSubject(refreshToken);
        String sessionId = jwtUtils.getSessionId(refreshToken);
        // 根据来源选择Redis Key
        String redisKey = (source == LoginSourceEnum.ADMIN)
                ? RedisConst.ADMIN_REFRESH_TOKEN + userId + ":" + sessionId
                : RedisConst.FRONT_REFRESH_TOKEN + userId + ":" + sessionId;
        // 检查Redis中的refreshToken是否一致
        String storedToken = redisUtils.get(redisKey, String.class);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(StatusEnum.NOT_LOGIN, AuthConst.REFRESH_TOKEN_EXPIRED);
        }
        // 根据来源生成新Token（复用同一sessionId，保持会话连续性）
        JwtUtils.TokenPair tokenPair;
        Long refreshTokenExpiration;
        if (source == LoginSourceEnum.ADMIN) {
            tokenPair = jwtUtils.generateAdminTokenPair(userId, sessionId);
            refreshTokenExpiration = jwtConfig.getAdmin().getRefreshTokenExpiration();
        } else {
            tokenPair = jwtUtils.generateFrontTokenPair(userId, sessionId);
            refreshTokenExpiration = jwtConfig.getFront().getRefreshTokenExpiration();
        }
        // 更新Redis中的refreshToken（设置与JWT相同的过期时间）
        redisUtils.set(redisKey, tokenPair.getRefreshToken(), refreshTokenExpiration, TimeUnit.MILLISECONDS);
        // 返回新token
        return TokenVO.builder()
                .accessToken(tokenPair.getAccessToken())
                .refreshToken(tokenPair.getRefreshToken())
                .build();
    }

    /**
     * 发送邮箱验证码
     *
     * @param dto 发送验证码请求参数
     */
    @Override
    public void sendCode(SendCodeDTO dto) {
        // 校验验证码用途
        CodePurposeEnum purpose = CodePurposeEnum.of(dto.getPurpose());
        if (purpose == null) {
            throw new BusinessException(AuthConst.INVALID_CODE_PURPOSE);
        }

        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        // 检查是否在短时间内重复发送
        String codeKey = RedisConst.EMAIL_CODE + purpose.getKey() + ":" + email;
        if (redisUtils.hasKey(codeKey)) {
            Long ttl = redisUtils.getExpire(codeKey);
            long resendThreshold = (AuthConst.CODE_EXPIRE_MINUTES * 60L) - AuthConst.CODE_RESEND_INTERVAL_SECONDS;
            if (ttl != null && ttl > resendThreshold) {
                throw new BusinessException(StatusEnum.REQUEST_FREQUENTLY, AuthConst.CODE_SEND_TOO_FREQUENT);
            }
        }

        // 生成验证码
        String code = VerifyCodeUtils.generate();

        // 存储到Redis
        redisUtils.set(codeKey, code, AuthConst.CODE_EXPIRE_MINUTES * 60L, TimeUnit.SECONDS);

        // 发送邮件到队列
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("expireMinutes", AuthConst.CODE_EXPIRE_MINUTES);
        params.put("sceneTitle", purpose.getSceneTitle());

        // 构建场景化邮件主题
        String subject = String.format("【%s】%s", blogConfig.getName(), purpose.getSceneTitle());

        EmailDTO emailDTO = EmailDTO.builder()
                .to(email)
                .type(EmailTypeEnum.VERIFY_CODE)
                .subject(subject)
                .params(params)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.EMAIL_ROUTING_KEY, emailDTO);
    }

    /**
     * 重置密码
     *
     * @param dto 重置密码请求参数
     */
    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String code = dto.getCode().trim();
        String newPassword = PasswordUtils.validateAndTrim(dto.getNewPassword());

        String codeKey = RedisConst.EMAIL_CODE + CodePurposeEnum.RESET_PASSWORD.getKey() + ":" + email;

        // 验证验证码
        String storedCode = redisUtils.get(codeKey, String.class);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
        }

        // 查询用户认证信息
        UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
        if (userAuth == null) {
            throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
        }

        // 校验新密码不能与原密码相同
        if (PasswordUtils.verify(newPassword, userAuth.getCredential())) {
            throw new BusinessException(AuthConst.NEW_PASSWORD_SAME_AS_OLD);
        }

        // 加密新密码并更新
        String encryptedPassword = PasswordUtils.encrypt(newPassword);
        authMapper.updateCredential(email, LoginTypeEnum.EMAIL.getCode(), encryptedPassword);

        // 删除验证码
        redisUtils.delete(codeKey);

        // 清除该用户所有会话（重置密码后强制所有设备重新登录）
        redisUtils.deleteByPrefix(RedisConst.FRONT_REFRESH_TOKEN + userAuth.getUserId() + ":");
        redisUtils.deleteByPrefix(RedisConst.ADMIN_REFRESH_TOKEN + userAuth.getUserId() + ":");
    }

    /**
     * 用户注册
     *
     * @param dto 注册请求参数
     * @return 登录结果（注册成功后自动登录）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String code = dto.getCode().trim();
        String password = PasswordUtils.validateAndTrim(dto.getPassword());

        String codeKey = RedisConst.EMAIL_CODE + CodePurposeEnum.REGISTER.getKey() + ":" + email;

        // 验证验证码
        String storedCode = redisUtils.get(codeKey, String.class);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
        }

        // 检查邮箱是否已注册
        UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
        if (existingAuth != null) {
            throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.EMAIL_ALREADY_REGISTERED);
        }

        // 生成用户ID
        String userId = UserUidUtils.generate();

        // 创建用户基础信息
        UserEntity user = UserEntity.builder()
                .userUid(userId)
                .status(AuthConst.USER_STATUS_NORMAL)
                .lastLoginTime(LocalDateTime.now())
                .build();
        userMapper.insertUser(user);

        // 创建用户认证信息
        String encryptedPassword = PasswordUtils.encrypt(password);
        UserAuthEntity userAuth = UserAuthEntity.builder()
                .userId(userId)
                .loginType(LoginTypeEnum.EMAIL.getCode())
                .identifier(email)
                .credential(encryptedPassword)
                .build();
        authMapper.insertUserAuth(userAuth);

        // 创建用户资料信息
        String defaultNickname = blogConfig.getDefaultNicknamePrefix() + userId.substring(0, 6);
        String defaultAvatar = websiteConfigService.getWebsiteConfig().getDefaultUserAvatar();
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .userId(userId)
                .nickname(defaultNickname)
                .avatar(defaultAvatar)
                .build();
        userMapper.insertUserProfile(userProfile);

        // 删除验证码
        redisUtils.delete(codeKey);

        // 清除用户总数缓存（下次查询会重新加载）
        redisUtils.delete(RedisConst.TOTAL_USER_COUNT);
        // 今日新增用户计数 +1
        String todayKey = RedisConst.TODAY_USER_COUNT + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long val = redisUtils.increment(todayKey);
        if (val != null && val == 1L) {
            redisUtils.expire(todayKey, RedisConst.EXPIRE_ONE_DAY * 2);
        }

        // 发送注册成功日志到MQ
        loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.REGISTER.getCode(), email, AuthConst.REGISTER_SUCCESS);
        // 发送自动登录日志到MQ（注册成功后自动登录）
        loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthActionEnum.LOGIN.getCode(), email, AuthConst.LOGIN_SUCCESS);

        // 自动登录，返回登录结果（注册只在前台）
        return buildLoginVO(userAuth, LoginSourceEnum.FRONT);
    }

    /**
     * 修改绑定邮箱
     *
     * @param userId 用户ID
     * @param dto    修改邮箱请求参数
     */
    @Override
    public void changeEmail(String userId, ChangeEmailDTO dto) {
        String newEmail = dto.getNewEmail().trim().toLowerCase(Locale.ROOT);
        String code = dto.getCode().trim();
        String codeKey = RedisConst.EMAIL_CODE + CodePurposeEnum.CHANGE_EMAIL.getKey() + ":" + newEmail;

        // 验证验证码
        String storedCode = redisUtils.get(codeKey, String.class);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new BusinessException(AuthConst.CODE_INVALID_OR_EXPIRED);
        }

        // 获取当前绑定的邮箱
        String currentEmail = authMapper.selectEmailByUserId(userId);

        // 检查新邮箱是否与当前邮箱相同
        if (newEmail.equals(currentEmail)) {
            throw new BusinessException(AuthConst.NEW_EMAIL_SAME_AS_OLD);
        }

        // 检查新邮箱是否已被其他账号使用
        UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(newEmail, LoginTypeEnum.EMAIL.getCode());
        if (existingAuth != null) {
            throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.EMAIL_ALREADY_REGISTERED);
        }

        // 更新邮箱
        authMapper.updateIdentifier(userId, LoginTypeEnum.EMAIL.getCode(), newEmail);

        // 删除验证码
        redisUtils.delete(codeKey);

        // 清除用户信息缓存
        redisUtils.delete(RedisConst.USER_INFO + userId);
        redisUtils.delete(RedisConst.USER_HOME_ME + userId);
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param dto    修改密码请求参数
     */
    @Override
    public void changePassword(String userId, ChangePasswordDTO dto) {
        // 查询用户邮箱认证信息
        UserAuthEntity userAuth = authMapper.selectEmailAuthByUserId(userId);
        if (userAuth == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        // 验证旧密码是否正确
        if (!PasswordUtils.verify(dto.getOldPassword(), userAuth.getCredential())) {
            throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.OLD_PASSWORD_ERROR);
        }

        // 校验新密码格式
        String newPassword = PasswordUtils.validateAndTrim(dto.getNewPassword());

        // 检查新密码是否与旧密码相同
        if (PasswordUtils.verify(newPassword, userAuth.getCredential())) {
            throw new BusinessException(AuthConst.NEW_PASSWORD_SAME_AS_OLD);
        }

        // 加密新密码并更新
        String encryptedPassword = PasswordUtils.encrypt(newPassword);
        authMapper.updateCredentialByUserId(userId, encryptedPassword);

        // 清除该用户所有会话（改密后强制所有设备重新登录）
        redisUtils.deleteByPrefix(RedisConst.FRONT_REFRESH_TOKEN + userId + ":");
        redisUtils.deleteByPrefix(RedisConst.ADMIN_REFRESH_TOKEN + userId + ":");
    }

    /**
     * 后台重置用户密码
     *
     * @param dto 重置密码请求参数
     */
    @Override
    public void adminResetPassword(AdminResetPasswordDTO dto) {
        String userId = dto.getUserId();
        String newPassword = PasswordUtils.validateAndTrim(dto.getPassword());

        // 校验用户是否存在
        UserAuthEntity userAuth = authMapper.selectEmailAuthByUserId(userId);
        if (userAuth == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        // 检查新密码是否与旧密码相同
        if (PasswordUtils.verify(newPassword, userAuth.getCredential())) {
            throw new BusinessException(AuthConst.NEW_PASSWORD_SAME_AS_OLD);
        }

        // 加密新密码并更新
        String encryptedPassword = PasswordUtils.encrypt(newPassword);
        authMapper.updateCredentialByUserId(userId, encryptedPassword);

        // 清除该用户所有会话（重置密码后强制所有设备重新登录）
        redisUtils.deleteByPrefix(RedisConst.FRONT_REFRESH_TOKEN + userId + ":");
        redisUtils.deleteByPrefix(RedisConst.ADMIN_REFRESH_TOKEN + userId + ":");
    }

    /**
     * 绑定QQ
     *
     * @param userId 用户ID
     * @param dto    绑定请求参数
     */
    @Override
    public void bindQQ(String userId, BindCodeDTO dto) {
        try {
            // 检查用户是否已绑定QQ
            if (authMapper.countByUserIdAndType(userId, LoginTypeEnum.QQ.getCode()) > 0) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.QQ_ALREADY_BINDDED);
            }

            // 用 code 换取 access_token
            String tokenUrl = String.format(AuthConst.QQ_ACCESS_TOKEN_URL,
                    oauthConfig.getQq().getAppId(),
                    oauthConfig.getQq().getAppKey(),
                    dto.getCode(),
                    URLEncoder.encode(oauthConfig.getQq().getRedirectUri(), StandardCharsets.UTF_8));
            String tokenResponse = restTemplate.getForObject(tokenUrl, String.class);

            if (tokenResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }
            JSONObject tokenJson = JSONObject.parseObject(tokenResponse);
            String accessToken = tokenJson.getString(AuthConst.QQ_RESPONSE_ACCESS_TOKEN);
            if (accessToken == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }

            // 用 access_token 获取 openId
            String openIdUrl = String.format(AuthConst.QQ_OPENID_URL, accessToken);
            String openIdResponse = restTemplate.getForObject(openIdUrl, String.class);
            if (openIdResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }
            JSONObject openIdJson = JSONObject.parseObject(openIdResponse);
            String openId = openIdJson.getString(AuthConst.QQ_RESPONSE_OPENID);
            if (openId == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.QQ_AUTH_FAILED);
            }

            // 检查该QQ是否已被其他用户绑定
            UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(openId, LoginTypeEnum.QQ.getCode());
            if (existingAuth != null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.QQ_ALREADY_BINDDED_BY_OTHER);
            }

            // 插入绑定记录
            UserAuthEntity userAuth = UserAuthEntity.builder()
                    .userId(userId)
                    .loginType(LoginTypeEnum.QQ.getCode())
                    .identifier(openId)
                    .build();
            authMapper.insertUserAuth(userAuth);

            // 清除用户信息缓存
            redisUtils.delete(RedisConst.USER_INFO + userId);
            redisUtils.delete(RedisConst.USER_HOME_ME + userId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("QQ绑定失败", e);
            throw new BusinessException(StatusEnum.FAILURE, AuthConst.QQ_BIND_FAILED);
        }
    }

    /**
     * 绑定微博
     *
     * @param userId 用户ID
     * @param dto    绑定请求参数
     */
    @Override
    public void bindWeibo(String userId, BindCodeDTO dto) {
        try {
            // 检查用户是否已绑定微博
            if (authMapper.countByUserIdAndType(userId, LoginTypeEnum.WEIBO.getCode()) > 0) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.WEIBO_ALREADY_BINDDED);
            }

            // 用 code 换取 access_token 和 uid
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", oauthConfig.getWeibo().getAppKey());
            params.add("client_secret", oauthConfig.getWeibo().getAppSecret());
            params.add("grant_type", "authorization_code");
            params.add("code", dto.getCode());
            params.add("redirect_uri", oauthConfig.getWeibo().getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            String tokenResponse = restTemplate.postForObject(
                    AuthConst.WEIBO_ACCESS_TOKEN_URL, request, String.class);

            if (tokenResponse == null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.WEIBO_AUTH_FAILED);
            }
            JSONObject tokenJson = JSONObject.parseObject(tokenResponse);
            String uid = String.valueOf(tokenJson.get(AuthConst.WEIBO_RESPONSE_UID));
            if (uid == null || "null".equals(uid)) {
                throw new BusinessException(StatusEnum.USERNAME_OR_PASSWORD_ERROR, AuthConst.WEIBO_AUTH_FAILED);
            }

            // 检查该微博是否已被其他用户绑定
            UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(uid, LoginTypeEnum.WEIBO.getCode());
            if (existingAuth != null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.WEIBO_ALREADY_BINDDED_BY_OTHER);
            }

            // 插入绑定记录
            UserAuthEntity userAuth = UserAuthEntity.builder()
                    .userId(userId)
                    .loginType(LoginTypeEnum.WEIBO.getCode())
                    .identifier(uid)
                    .build();
            authMapper.insertUserAuth(userAuth);

            // 清除用户信息缓存
            redisUtils.delete(RedisConst.USER_INFO + userId);
            redisUtils.delete(RedisConst.USER_HOME_ME + userId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微博绑定失败", e);
            throw new BusinessException(StatusEnum.FAILURE, AuthConst.WEIBO_BIND_FAILED);
        }
    }

    /**
     * 解绑QQ
     *
     * @param userId 用户ID
     */
    @Override
    public void unbindQQ(String userId) {
        try {
            // 检查用户是否已绑定QQ
            if (authMapper.countByUserIdAndType(userId, LoginTypeEnum.QQ.getCode()) == 0) {
                throw new BusinessException(AuthConst.QQ_NOT_BINDDED_YET);
            }

            // 删除绑定记录
            authMapper.deleteByUserIdAndType(userId, LoginTypeEnum.QQ.getCode());

            // 清除用户信息缓存
            redisUtils.delete(RedisConst.USER_INFO + userId);
            redisUtils.delete(RedisConst.USER_HOME_ME + userId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("QQ解绑失败", e);
            throw new BusinessException(StatusEnum.FAILURE, AuthConst.QQ_UNBIND_FAILED);
        }
    }

    /**
     * 解绑微博
     *
     * @param userId 用户ID
     */
    @Override
    public void unbindWeibo(String userId) {
        try {
            // 检查用户是否已绑定微博
            if (authMapper.countByUserIdAndType(userId, LoginTypeEnum.WEIBO.getCode()) == 0) {
                throw new BusinessException(AuthConst.WEIBO_NOT_BINDDED_YET);
            }

            // 删除绑定记录
            authMapper.deleteByUserIdAndType(userId, LoginTypeEnum.WEIBO.getCode());

            // 清除用户信息缓存
            redisUtils.delete(RedisConst.USER_INFO + userId);
            redisUtils.delete(RedisConst.USER_HOME_ME + userId);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微博解绑失败", e);
            throw new BusinessException(StatusEnum.FAILURE, AuthConst.WEIBO_UNBIND_FAILED);
        }
    }

}
