package com.lsstop.service.impl;

import com.lsstop.constant.AuthConst;
import com.lsstop.constant.CommonConst;
import com.lsstop.constant.MenuConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.AddUserDTO;
import com.lsstop.domain.dto.UpdateUserApiPermissionDTO;
import com.lsstop.domain.dto.UpdateUserDTO;
import com.lsstop.domain.dto.UpdateUserInfoDTO;
import com.lsstop.domain.dto.UpdateUserMenuDTO;
import com.lsstop.domain.dto.EmailDTO;
import com.lsstop.domain.entity.UserAuthEntity;
import com.lsstop.domain.entity.UserEntity;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.AdminUserInfoVO;
import com.lsstop.domain.vo.RoleOptionVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.domain.vo.UserManageRoleVO;
import com.lsstop.domain.vo.UserManageVO;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.enums.EmailTypeEnum;
import com.lsstop.enums.FileFolderEnum;
import com.lsstop.enums.LoginTypeEnum;
import com.lsstop.enums.StatusEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.ApiPermissionMapper;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.mapper.LikeMapper;
import com.lsstop.mapper.LoginLogMapper;
import com.lsstop.mapper.MenuMapper;
import com.lsstop.mapper.RoleMapper;
import com.lsstop.mapper.UserMapper;
import com.lsstop.service.CosService;
import com.lsstop.service.UserService;
import com.lsstop.utils.PasswordUtils;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.StringUtils;
import com.lsstop.utils.UserUidUtils;
import com.lsstop.utils.ValidateUtils;
import com.lsstop.service.WebsiteConfigService;
import com.lsstop.constant.RabbitMQConst;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author lishusheng
 * @date 2026/01/03
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private AuthMapper authMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private LoginLogMapper loginLogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private CosService cosService;

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private ApiPermissionMapper apiPermissionMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private WebsiteConfigService websiteConfigService;

    /**
     * 根据用户ID获取用户资料
     *
     * @param userId 用户ID
     * @return 用户资料信息
     */
    @Override
    public UserInfoVO getUserProfile(String userId) {
        String cacheKey = RedisConst.USER_INFO + userId;
        // 先从缓存获取
        UserInfoVO cachedUser = redisUtils.get(cacheKey, UserInfoVO.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 缓存不存在，查询数据库
        UserProfileEntity userProfile = userMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = userProfile.asViewObject(UserInfoVO.class);
        // 写入缓存，过期时间1小时
        redisUtils.set(cacheKey, userInfoVO, RedisConst.EXPIRE_ONE_HOUR);
        return userInfoVO;
    }

    /**
     * 获取用户公开主页详情（查看他人）
     *
     * @param userId 用户 ID
     * @return 用户公开主页详情
     */
    @Override
    public UserPublicProfileVO getUserHomeDetail(String userId) {
        // 校验userId
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(StatusEnum.PARAM_ERROR, CommonConst.USER_ID_REQUIRED);
        }
        String cacheKey = RedisConst.USER_HOME_PUBLIC + userId;
        // 先从缓存获取
        UserPublicProfileVO cachedUser = redisUtils.get(cacheKey, UserPublicProfileVO.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 缓存不存在，查询数据库
        UserPublicProfileVO userPublicProfileVO = userMapper.selectUserPublicHomeDetail(userId);
        if (userPublicProfileVO == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }
        // 查询评论数量
        Integer commentCount = commentMapper.countByUserId(userId);
        userPublicProfileVO.setCommentCount(commentCount != null ? commentCount : 0);
        // 查询获赞数量
        Integer likeCount = likeMapper.countLikesByUserId(userId);
        userPublicProfileVO.setLikeCount(likeCount != null ? likeCount : 0);
        // 查询 IP 归属地
        String ipRegion = loginLogMapper.selectLatestIpRegionByUserId(userId);
        userPublicProfileVO.setIpRegion(ipRegion != null ? ipRegion : CommonConst.UNKNOWN_REGION);
        // 写入缓存，过期时间 1 小时
        redisUtils.set(cacheKey, userPublicProfileVO, RedisConst.EXPIRE_ONE_HOUR);
        return userPublicProfileVO;
    }

    /**
     * 获取当前用户主页详情（查看自己）
     *
     * @param userId 用户 ID
     * @return 用户完整主页详情
     */
    @Override
    public UserProfileVO getMyHomeDetail(String userId) {
        String cacheKey = RedisConst.USER_HOME_ME + userId;
        // 先从缓存获取
        UserProfileVO cachedUser = redisUtils.get(cacheKey, UserProfileVO.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 缓存不存在，查询数据库
        UserProfileVO userProfileVO = userMapper.selectUserHomeDetail(userId);
        if (userProfileVO == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }
        // 查询评论数量
        Integer commentCount = commentMapper.countByUserId(userId);
        userProfileVO.setCommentCount(commentCount != null ? commentCount : 0);
        // 查询获赞数量
        Integer likeCount = likeMapper.countLikesByUserId(userId);
        userProfileVO.setLikeCount(likeCount != null ? likeCount : 0);
        // 查询 IP 归属地
        String ipRegion = loginLogMapper.selectLatestIpRegionByUserId(userId);
        userProfileVO.setIpRegion(ipRegion != null ? ipRegion : CommonConst.UNKNOWN_REGION);
        // 邮箱脱敏处理（在写入缓存前脱敏，避免敏感信息入缓存）
        if (userProfileVO.getEmail() != null) {
            userProfileVO.setEmail(StringUtils.maskEmail(userProfileVO.getEmail()));
        }
        // 写入缓存，过期时间 1 小时
        redisUtils.set(cacheKey, userProfileVO, RedisConst.EXPIRE_ONE_HOUR);
        return userProfileVO;
    }

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param file   头像文件
     * @return 新头像URL
     */
    @Override
    public String updateAvatar(String userId, MultipartFile file) {
        // 上传头像到COS
        String avatarUrl = cosService.uploadImage(file, FileFolderEnum.AVATAR.getFolder());
        try {
            // 更新数据库
            int rows = userMapper.updateAvatar(userId, avatarUrl);
            if (rows == 0) {
                throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            // 数据库更新失败，删除已上传的文件
            cosService.deleteFile(avatarUrl);
            throw e;
        }
        // 清除用户相关缓存
        clearUserCache(userId);
        return avatarUrl;
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param dto    更新用户信息参数
     */
    @Override
    public void updateUserInfo(String userId, UpdateUserInfoDTO dto) {
        // 昵称去除前后空格
        String nickname = dto.getNickname().trim();
        // 校验个人网站格式
        String website = dto.getWebsite();
        if (website != null && !website.isBlank()) {
            website = website.trim();
            if (!StringUtils.isValidUrl(website)) {
                throw new BusinessException(AuthConst.WEBSITE_FORMAT_INVALID);
            }
        } else {
            website = null;
        }
        // 校验个人简介，避免全空格
        String intro = dto.getIntro();
        if (intro != null && !intro.isBlank()) {
            intro = intro.trim();
        } else {
            intro = null;
        }
        // 更新数据库
        int rows = userMapper.updateUserInfo(userId, nickname, website, intro);
        if (rows == 0) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }
        // 清除用户相关缓存
        clearUserCache(userId);
    }

    /**
     * 后台新增用户
     *
     * @param dto 新增用户参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(AddUserDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);

        // 1. 校验邮箱是否已被注册
        UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
        if (existingAuth != null) {
            throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.EMAIL_ALREADY_REGISTERED);
        }

        // 2. 校验密码格式
        String password = PasswordUtils.validateAndTrim(dto.getPassword());

        // 3. 校验个人网站格式
        String website = ValidateUtils.validateWebsite(dto.getWebsite());

        // 4. 处理个人简介
        String intro = ValidateUtils.validateIntro(dto.getIntro());

        // 5. 校验状态值
        ValidateUtils.validateStatus(dto.getStatus());

        // 6. 校验角色ID是否存在且启用
        List<Integer> enabledRoleIds = roleMapper.selectAllRoleOptions()
                .stream()
                .map(RoleOptionVO::getId)
                .toList();
        for (Integer roleId : dto.getRoleIds()) {
            if (!enabledRoleIds.contains(roleId)) {
                throw new BusinessException(StatusEnum.PARAM_ERROR, AuthConst.ROLE_NOT_FOUND_OR_DISABLED);
            }
        }

        // 7. 生成用户ID
        String userId = UserUidUtils.generate();

        // 8. 创建用户基础信息（blog_user）
        UserEntity user = UserEntity.builder()
                .userUid(userId)
                .status(dto.getStatus())
                .lastLoginTime(LocalDateTime.now())
                .build();
        userMapper.insertUser(user);

        // 9. 创建用户认证信息（blog_user_auth）
        String encryptedPassword = PasswordUtils.encrypt(password);
        UserAuthEntity userAuth = UserAuthEntity.builder()
                .userId(userId)
                .loginType(LoginTypeEnum.EMAIL.getCode())
                .identifier(email)
                .credential(encryptedPassword)
                .build();
        authMapper.insertUserAuth(userAuth);

        // 10. 创建用户资料信息（blog_user_profile）
        String nickname = dto.getNickname().trim();
        String avatar = dto.getAvatar();
        if (avatar == null || avatar.isBlank()) {
            avatar = websiteConfigService.getWebsiteConfig().getDefaultUserAvatar();
        }
        UserProfileEntity userProfile = UserProfileEntity.builder()
                .userId(userId)
                .nickname(nickname)
                .avatar(avatar)
                .website(website)
                .intro(intro)
                .build();
        userMapper.insertUserProfile(userProfile);

        // 11. 分配角色（blog_user_role）
        userMapper.batchInsertUserRole(userId, dto.getRoleIds());

        // 12. 发送账号开通通知邮件
        Map<String, Object> emailParams = new HashMap<>();
        emailParams.put("nickname", nickname);
        emailParams.put("email", email);
        emailParams.put("password", password);
        EmailDTO emailDTO = EmailDTO.builder()
                .to(email)
                .type(EmailTypeEnum.WELCOME)
                .params(emailParams)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConst.BLOG_EXCHANGE, RabbitMQConst.EMAIL_ROUTING_KEY, emailDTO);

        // 13. 清理相关缓存
        redisUtils.delete(RedisConst.TOTAL_USER_COUNT);
        String todayKey = RedisConst.TODAY_USER_COUNT + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long val = redisUtils.increment(todayKey);
        if (val != null && val == 1L) {
            redisUtils.expire(todayKey, RedisConst.EXPIRE_ONE_DAY * 2);
        }
    }

    /**
     * 后台更新用户
     *
     * @param dto 更新用户参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UpdateUserDTO dto) {
        String userId = dto.getUserId();

        // 1. 校验用户是否存在
        UserProfileEntity userProfile = userMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        // 2. 如果邮箱变更，校验新邮箱是否已被其他账号使用
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        String currentEmail = authMapper.selectEmailByUserId(userId);
        if (!email.equals(currentEmail)) {
            UserAuthEntity existingAuth = authMapper.selectByIdentifierAndType(email, LoginTypeEnum.EMAIL.getCode());
            if (existingAuth != null) {
                throw new BusinessException(StatusEnum.USERNAME_OR_EMAIL_EXIST, AuthConst.EMAIL_ALREADY_REGISTERED);
            }
        }

        // 3. 校验网站、简介、状态（复用 ValidateUtils）
        String website = ValidateUtils.validateWebsite(dto.getWebsite());
        String intro = ValidateUtils.validateIntro(dto.getIntro());
        ValidateUtils.validateStatus(dto.getStatus());

        // 4. 校验角色ID是否存在且启用
        List<Integer> enabledRoleIds = roleMapper.selectAllRoleOptions()
                .stream()
                .map(RoleOptionVO::getId)
                .toList();
        for (Integer roleId : dto.getRoleIds()) {
            if (!enabledRoleIds.contains(roleId)) {
                throw new BusinessException(StatusEnum.PARAM_ERROR, AuthConst.ROLE_NOT_FOUND_OR_DISABLED);
            }
        }

        // 5. 处理头像
        String avatar = dto.getAvatar();
        if (avatar == null || avatar.isBlank()) {
            avatar = websiteConfigService.getWebsiteConfig().getDefaultUserAvatar();
        }

        // 6. 更新用户状态（blog_user）
        int statusRows = userMapper.updateUserStatus(userId, dto.getStatus());
        if (statusRows == 0) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        // 7. 更新用户资料（blog_user_profile）
        String nickname = dto.getNickname().trim();
        int profileRows = userMapper.updateUserProfile(userId, nickname, avatar, website, intro);
        if (profileRows == 0) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        // 8. 如果邮箱变更，更新认证信息（blog_user_auth）
        if (!email.equals(currentEmail)) {
            authMapper.updateIdentifier(userId, LoginTypeEnum.EMAIL.getCode(), email);
        }

        // 9. 差量更新角色（blog_user_role）
        List<Integer> currentRoleIds = userMapper.selectRoleIdsByUserId(userId);
        Set<Integer> currentSet = new HashSet<>(currentRoleIds != null ? currentRoleIds : Collections.emptySet());
        Set<Integer> newSet = new HashSet<>(dto.getRoleIds());

        // 计算需要新增的
        Set<Integer> toAdd = new HashSet<>(newSet);
        toAdd.removeAll(currentSet);
        if (!toAdd.isEmpty()) {
            userMapper.batchInsertUserRole(userId, new ArrayList<>(toAdd));
        }

        // 计算需要软删除的
        Set<Integer> toRemove = new HashSet<>(currentSet);
        toRemove.removeAll(newSet);
        if (!toRemove.isEmpty()) {
            userMapper.batchSoftDeleteUserRole(userId, new ArrayList<>(toRemove), System.currentTimeMillis());
        }

        // 10. 清除用户相关缓存
        clearUserCache(userId);
        clearUserMenuCache(userId);
        clearUserApiPermissionCache(userId);
    }

    /**
     * 修改用户菜单权限（逐类型差集更新）
     * <p>管理员提交用户最终应看到的菜单ID集合，后端与角色菜单做差集：
     * type=1（额外授予）= new − role，type=2（额外排除）= role − new
     *
     * @param dto 修改用户菜单权限参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserMenuPermission(UpdateUserMenuDTO dto) {
        String userId = dto.getUserId();
        // 校验用户是否存在
        UserProfileEntity userProfile = userMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        Set<Integer> newSet = new HashSet<>(dto.getMenuIds());
        // 查询角色菜单
        List<Integer> roleMenuIds = menuMapper.selectRoleMenuIdsByUserId(userId);
        Set<Integer> roleSet = new HashSet<>(roleMenuIds != null ? roleMenuIds : Collections.emptySet());

        // 计算 type=1（额外授予）：new − role
        Set<Integer> grants = new HashSet<>(newSet);
        grants.removeAll(roleSet);

        // 计算 type=2（额外排除）：role − new
        Set<Integer> excludes = new HashSet<>(roleSet);
        excludes.removeAll(newSet);

        long now = System.currentTimeMillis();
        // 逐类型差集更新
        syncUserMenuDiffs(userId, grants, MenuConst.USER_MENU_TYPE_GRANT, now);
        syncUserMenuDiffs(userId, excludes, MenuConst.USER_MENU_TYPE_EXCLUDE, now);

        // 清除用户菜单缓存
        clearUserMenuCache(userId);
    }

    /**
     * 同步用户菜单调整记录的差集
     *
     * @param userId    用户ID
     * @param desired   目标菜单ID集合
     * @param type      调整类型：1-额外授予 2-额外排除
     * @param deletedAt 删除时间戳
     */
    private void syncUserMenuDiffs(String userId, Set<Integer> desired, int type, long deletedAt) {
        List<Integer> current = menuMapper.selectUserMenuIdsByType(userId, type);
        Set<Integer> currentSet = new HashSet<>(current != null ? current : Collections.emptySet());

        // 计算新增的（目标有，当前无）
        Set<Integer> toInsert = new HashSet<>(desired);
        toInsert.removeAll(currentSet);

        // 计算软删除的（当前有，目标无）
        Set<Integer> toSoftDelete = new HashSet<>(currentSet);
        toSoftDelete.removeAll(desired);

        if (!toInsert.isEmpty()) {
            menuMapper.batchInsertUserMenu(userId, new ArrayList<>(toInsert), type);
        }
        if (!toSoftDelete.isEmpty()) {
            menuMapper.batchSoftDeleteUserMenu(userId, new ArrayList<>(toSoftDelete), type, deletedAt);
        }
    }

    /**
     * 修改用户接口权限（逐类型差集更新）
     * <p>管理员提交用户最终应看到的接口权限ID集合，后端与角色接口权限做差集：
     * type=1（额外授予）= new − role，type=2（额外排除）= role − new
     *
     * @param dto 修改用户接口权限参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserApiPermission(UpdateUserApiPermissionDTO dto) {
        String userId = dto.getUserId();
        // 校验用户是否存在
        UserProfileEntity userProfile = userMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }

        Set<Integer> newSet = new HashSet<>(dto.getApiIds());
        // 查询角色接口权限
        List<Integer> roleApiIds = apiPermissionMapper.selectRoleApiPermissionIdsByUserId(userId);
        Set<Integer> roleSet = new HashSet<>(roleApiIds != null ? roleApiIds : Collections.emptySet());

        // 计算 type=1（额外授予）：new − role
        Set<Integer> grants = new HashSet<>(newSet);
        grants.removeAll(roleSet);

        // 计算 type=2（额外排除）：role − new
        Set<Integer> excludes = new HashSet<>(roleSet);
        excludes.removeAll(newSet);

        long now = System.currentTimeMillis();
        // 逐类型差集更新
        syncUserApiDiffs(userId, grants, MenuConst.USER_MENU_TYPE_GRANT, now);
        syncUserApiDiffs(userId, excludes, MenuConst.USER_MENU_TYPE_EXCLUDE, now);

        // 清除用户接口权限缓存
        clearUserApiPermissionCache(userId);
    }

    /**
     * 同步用户接口权限调整记录的差集
     */
    private void syncUserApiDiffs(String userId, Set<Integer> desired, int type, long deletedAt) {
        List<Integer> current = apiPermissionMapper.selectUserApiPermissionIdsByType(userId, type);
        Set<Integer> currentSet = new HashSet<>(current != null ? current : Collections.emptySet());

        Set<Integer> toInsert = new HashSet<>(desired);
        toInsert.removeAll(currentSet);

        Set<Integer> toSoftDelete = new HashSet<>(currentSet);
        toSoftDelete.removeAll(desired);

        if (!toInsert.isEmpty()) {
            apiPermissionMapper.batchInsertUserApiPermission(userId, new ArrayList<>(toInsert), type);
        }
        if (!toSoftDelete.isEmpty()) {
            apiPermissionMapper.batchSoftDeleteUserApiPermission(userId, new ArrayList<>(toSoftDelete), type, deletedAt);
        }
    }

    /**
     * 获取后台当前登录用户信息
     *
     * @param userId 用户ID
     * @return 后台用户信息
     */
    @Override
    public AdminUserInfoVO getAdminUserInfo(String userId) {
        // 查询用户资料
        UserProfileEntity userProfile = userMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(StatusEnum.NOT_FOUND, AuthConst.USER_NOT_FOUND);
        }
        AdminUserInfoVO vo = userProfile.asViewObject(AdminUserInfoVO.class);
        // 查询用户邮箱
        String email = authMapper.selectEmailByUserId(userId);
        vo.setEmail(email);
        return vo;
    }

    /**
     * 清除用户相关缓存
     *
     * @param userId 用户ID
     */
    private void clearUserCache(String userId) {
        redisUtils.delete(RedisConst.USER_INFO + userId);
        redisUtils.delete(RedisConst.USER_HOME_ME + userId);
        redisUtils.delete(RedisConst.USER_HOME_PUBLIC + userId);
    }

    /**
     * 清除用户菜单相关缓存
     *
     * @param userId 用户ID
     */
    private void clearUserMenuCache(String userId) {
        redisUtils.delete(RedisConst.USER_MENU_TREE + userId);
        redisUtils.delete(RedisConst.USER_MENU_IDS + userId);
    }

    /**
     * 清除用户接口权限相关缓存
     */
    private void clearUserApiPermissionCache(String userId) {
        redisUtils.delete(RedisConst.USER_EFFECTIVE_API_PERMISSIONS + userId);
        redisUtils.delete(RedisConst.USER_API_PERMISSION_IDS + userId);
    }

    /**
     * 分页查询用户管理列表
     *
     * @param current  当前页码
     * @param size     每页数量
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户列表
     */
    @Override
    public List<UserManageVO> listUsers(Integer current, Integer size, String userId, String nickname, String email, Integer status) {
        int offset = (current - 1) * size;
        List<UserManageVO> users = userMapper.selectUserList(offset, size, userId, nickname, email, status);
        if (users.isEmpty()) {
            return users;
        }
        // 批量查询角色并分组
        List<String> userIds = users.stream().map(UserManageVO::getUserId).toList();
        List<UserManageRoleVO> allRoles = userMapper.selectRolesByUserIds(userIds);
        Map<String, List<UserManageRoleVO>> rolesByUserId = allRoles.stream()
                .collect(Collectors.groupingBy(UserManageRoleVO::getUserId));
        // 组装角色列表
        for (UserManageVO user : users) {
            user.setRoles(rolesByUserId.getOrDefault(user.getUserId(), Collections.emptyList()));
        }
        return users;
    }

    /**
     * 统计用户总数
     *
     * @param userId   用户ID（精确匹配）
     * @param nickname 昵称（模糊搜索）
     * @param email    邮箱（模糊搜索）
     * @param status   状态（0-禁用 1-正常）
     * @return 用户总数
     */
    @Override
    public Integer countUserTotal(String userId, String nickname, String email, Integer status) {
        return userMapper.countUserTotal(userId, nickname, email, status);
    }

}
