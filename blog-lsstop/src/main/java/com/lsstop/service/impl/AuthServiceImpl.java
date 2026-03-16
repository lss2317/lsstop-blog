package com.lsstop.service.impl;

import com.lsstop.config.BlogConfig;
import com.lsstop.constant.AuthConst;
import com.lsstop.constant.RabbitMQConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.EmailDTO;
import com.lsstop.domain.dto.ChangeEmailDTO;
import com.lsstop.domain.dto.ChangePasswordDTO;
import com.lsstop.domain.entity.UserAuthEntity;
import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.dto.EmailCodeLoginDTO;
import com.lsstop.domain.dto.EmailLoginDTO;
import com.lsstop.domain.dto.QQLoginDTO;
import com.lsstop.domain.dto.RegisterDTO;
import com.lsstop.domain.dto.ResetPasswordDTO;
import com.lsstop.domain.dto.SendCodeDTO;
import com.lsstop.domain.dto.WeiboLoginDTO;
import com.lsstop.domain.vo.LoginVO;
import com.lsstop.domain.vo.TokenVO;
import com.lsstop.enums.CodePurposeEnum;
import com.lsstop.enums.EmailTypeEnum;
import com.lsstop.enums.LoginResultEnum;
import com.lsstop.enums.LoginSourceEnum;
import com.lsstop.enums.LoginTypeEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.service.AuthService;
import com.lsstop.service.LoginLogService;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.utils.VerifyCodeUtils;
import com.lsstop.utils.JwtUtils;
import com.lsstop.utils.PasswordUtils;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.UserUidUtils;
import com.lsstop.config.JwtConfig;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private AuthMapper authMapper;

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
    private WebsiteConfigService websiteConfigService;

    /**
     * 邮箱密码登录
     *
     * @param dto 邮箱登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO emailLogin(EmailLoginDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String password = dto.getPassword();
        String userId = null;

        try {
            // 查询用户认证信息
            UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
            if (userAuth == null) {
                throw new BusinessException(AuthConst.ACCOUNT_OR_PASSWORD_ERROR);
            }
            userId = userAuth.getUserId();

            // 验证密码
            if (!PasswordUtils.verify(password, userAuth.getCredential())) {
                throw new BusinessException(AuthConst.ACCOUNT_OR_PASSWORD_ERROR);
            }

            // 检查用户是否被禁用
            UserEntity user = authMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            authMapper.updateLastLoginTime(userId);

            // 发送登录成功日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth);
        } catch (BusinessException e) {
            // 发送登录失败日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.FAIL.getCode(), e.getMessage());
            throw e;
        }
    }

    /**
     * 邮箱验证码登录
     *
     * @param dto 验证码登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO emailCodeLogin(EmailCodeLoginDTO dto) {
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
            UserEntity user = authMapper.selectUserById(userId);
            if (user == null || AuthConst.USER_STATUS_DISABLED.equals(user.getStatus())) {
                throw new BusinessException(AuthConst.ACCOUNT_DISABLED);
            }

            // 更新最后登录时间
            authMapper.updateLastLoginTime(userId);

            // 登录成功后删除验证码
            redisUtils.delete(codeKey);

            // 发送登录成功日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthConst.LOGIN_SUCCESS);

            return buildLoginVO(userAuth);
        } catch (BusinessException e) {
            // 发送登录失败日志到MQ
            loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.FAIL.getCode(), e.getMessage());
            throw e;
        }
    }

    /**
     * QQ登录
     *
     * @param dto QQ登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO qqLogin(QQLoginDTO dto) {
        // TODO: 调用QQ开放平台接口验证access_token
        // 1. 使用accessToken调用QQ接口获取openId
        // 2. 根据 openId 查询用户认证信息
        // UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(openId, LoginTypeEnum.QQ.getCode());
        // 3. 如果用户不存在，可考虑自动注册
        // 4. 返回登录结果
        // return buildLoginVO(userAuth);
        throw new BusinessException("QQ登录功能暂未实现");
    }

    /**
     * 微博登录
     *
     * @param dto 微博登录参数
     * @return 用户信息
     */
    @Override
    public LoginVO weiboLogin(WeiboLoginDTO dto) {
        // TODO: 调用微博开放平台接口验证access_token
        // 1. 使用accessToken调用微博接口获取uid
        // 2. 根据 uid 查询用户认证信息
        // UserAuthEntity userAuth = authMapper.selectByIdentifierAndType(uid, LoginTypeEnum.WEIBO.getCode());
        // 3. 如果用户不存在，可考虑自动注册
        // 4. 返回登录结果
        // return buildLoginVO(userAuth);
        throw new BusinessException("微博登录功能暂未实现");
    }

    /**
     * 构建登录返回结果
     *
     * @param userAuth 用户认证信息
     * @return 登录结果
     */
    private LoginVO buildLoginVO(UserAuthEntity userAuth) {
        // 查询用户资料
        UserProfileEntity userProfileEntity = authMapper.selectProfileById(userAuth.getUserId());
        LoginVO loginVO = userProfileEntity.asViewObject(LoginVO.class);

        // 生成token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userAuth.getUserId());
        loginVO.setAccessToken(tokenPair.getAccessToken());
        loginVO.setRefreshToken(tokenPair.getRefreshToken());

        // 存储refreshToken到Redis（设置与JWT相同的过期时间）
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userAuth.getUserId(),
                tokenPair.getRefreshToken(),
                jwtConfig.getFront().getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS);

        return loginVO;
    }

    /**
     * 用户登出
     * <p>根据refreshToken解析用户ID，清除Redis中存储的refreshToken</p>
     * <p>宽松处理：即使token过期或无效，也不报错，确保用户能正常退出</p>
     *
     * @param refreshToken 刷新令牌
     */
    @Override
    public void logout(String refreshToken) {
        try {
            String userId = jwtUtils.getSubjectIgnoreExpiration(refreshToken);
            if (userId != null) {
                // 校验传入的token与Redis中存储的是否一致，防止旧token踢下线
                String storedToken = redisUtils.get(RedisConst.FRONT_REFRESH_TOKEN + userId, String.class);
                if (storedToken != null && storedToken.equals(refreshToken)) {
                    redisUtils.delete(RedisConst.FRONT_REFRESH_TOKEN + userId);
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
     * @return 新的token信息
     */
    @Override
    public TokenVO refreshToken(String refreshToken) {
        // 验证refreshToken
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BusinessException(AuthConst.REFRESH_TOKEN_INVALID);
        }
        // 获取用户ID
        String userId = jwtUtils.getSubject(refreshToken);
        // 检查Redis中的refreshToken是否一致
        String storedToken = redisUtils.get(RedisConst.FRONT_REFRESH_TOKEN + userId, String.class);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(AuthConst.REFRESH_TOKEN_EXPIRED);
        }
        // 生成新token
        JwtUtils.TokenPair tokenPair = jwtUtils.generateFrontTokenPair(userId);
        // 更新Redis中的refreshToken（设置与JWT相同的过期时间）
        redisUtils.set(RedisConst.FRONT_REFRESH_TOKEN + userId,
                tokenPair.getRefreshToken(),
                jwtConfig.getFront().getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS);
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
                throw new BusinessException(AuthConst.CODE_SEND_TOO_FREQUENT);
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
            throw new BusinessException(AuthConst.EMAIL_ALREADY_REGISTERED);
        }

        // 生成用户ID
        String userId = UserUidUtils.generate();

        // 创建用户基础信息
        UserEntity user = UserEntity.builder()
                .userUid(userId)
                .status(AuthConst.USER_STATUS_NORMAL)
                .lastLoginTime(LocalDateTime.now())
                .build();
        authMapper.insertUser(user);

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
        authMapper.insertUserProfile(userProfile);

        // 删除验证码
        redisUtils.delete(codeKey);

        // 发送登录成功日志到MQ
        loginLogService.sendLoginLog(userId, LoginTypeEnum.EMAIL.getCode(), LoginSourceEnum.FRONT.getCode(), LoginResultEnum.SUCCESS.getCode(), AuthConst.LOGIN_SUCCESS);

        // 自动登录，返回登录结果
        return buildLoginVO(userAuth);
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
            throw new BusinessException(AuthConst.EMAIL_ALREADY_REGISTERED);
        }

        // 更新邮箱
        authMapper.updateIdentifier(userId, LoginTypeEnum.EMAIL.getCode(), newEmail);

        // 删除验证码
        redisUtils.delete(codeKey);
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
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }

        // 验证旧密码是否正确
        if (!PasswordUtils.verify(dto.getOldPassword(), userAuth.getCredential())) {
            throw new BusinessException(AuthConst.OLD_PASSWORD_ERROR);
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
    }

}
