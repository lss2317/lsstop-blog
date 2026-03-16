package com.lsstop.service.impl;

import com.lsstop.constant.AuthConst;
import com.lsstop.constant.RedisConst;
import com.lsstop.domain.dto.UpdateUserInfoDTO;
import com.lsstop.domain.entity.UserProfileEntity;
import com.lsstop.domain.vo.UserProfileVO;
import com.lsstop.domain.vo.UserPublicProfileVO;
import com.lsstop.domain.vo.UserInfoVO;
import com.lsstop.enums.FileFolderEnum;
import com.lsstop.exception.BusinessException;
import com.lsstop.mapper.AuthMapper;
import com.lsstop.mapper.CommentMapper;
import com.lsstop.mapper.LikeMapper;
import com.lsstop.service.CosService;
import com.lsstop.service.UserService;
import com.lsstop.utils.RedisUtils;
import com.lsstop.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private RedisUtils redisUtils;

    @Resource
    private CosService cosService;

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
        UserProfileEntity userProfile = authMapper.selectProfileById(userId);
        if (userProfile == null) {
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }
        UserInfoVO userInfoVO = userProfile.asViewObject(UserInfoVO.class);
        // 写入缓存，过期时间1小时
        redisUtils.set(cacheKey, userInfoVO, RedisConst.EXPIRE_ONE_HOUR);
        return userInfoVO;
    }

    /**
     * 获取用户公开主页详情（查看他人）
     *
     * @param userId 用户ID
     * @return 用户公开主页详情
     */
    @Override
    public UserPublicProfileVO getUserHomeDetail(String userId) {
        String cacheKey = RedisConst.USER_HOME_PUBLIC + userId;
        // 先从缓存获取
        UserPublicProfileVO cachedUser = redisUtils.get(cacheKey, UserPublicProfileVO.class);
        if (cachedUser != null) {
            return cachedUser;
        }
        // 缓存不存在，查询数据库
        UserPublicProfileVO userPublicProfileVO = authMapper.selectUserPublicHomeDetail(userId);
        if (userPublicProfileVO == null) {
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }
        // 查询评论数量
        Integer commentCount = commentMapper.countByUserId(userId);
        userPublicProfileVO.setCommentCount(commentCount != null ? commentCount : 0);
        // 查询获赞数量
        Integer likeCount = likeMapper.countLikesByUserId(userId);
        userPublicProfileVO.setLikeCount(likeCount != null ? likeCount : 0);
        // 写入缓存，过期时间1小时
        redisUtils.set(cacheKey, userPublicProfileVO, RedisConst.EXPIRE_ONE_HOUR);
        return userPublicProfileVO;
    }

    /**
     * 获取当前用户主页详情（查看自己）
     *
     * @param userId 用户ID
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
        UserProfileVO userProfileVO = authMapper.selectUserHomeDetail(userId);
        if (userProfileVO == null) {
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }
        // 邮箱脱敏处理
        if (userProfileVO.getEmail() != null) {
            userProfileVO.setEmail(StringUtils.maskEmail(userProfileVO.getEmail()));
        }
        // 查询评论数量
        Integer commentCount = commentMapper.countByUserId(userId);
        userProfileVO.setCommentCount(commentCount != null ? commentCount : 0);
        // 查询获赞数量
        Integer likeCount = likeMapper.countLikesByUserId(userId);
        userProfileVO.setLikeCount(likeCount != null ? likeCount : 0);
        // 写入缓存，过期时间1小时
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
            int rows = authMapper.updateAvatar(userId, avatarUrl);
            if (rows == 0) {
                throw new BusinessException(AuthConst.USER_NOT_FOUND);
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
        int rows = authMapper.updateUserInfo(userId, nickname, website, intro);
        if (rows == 0) {
            throw new BusinessException(AuthConst.USER_NOT_FOUND);
        }
        // 清除用户相关缓存
        clearUserCache(userId);
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

}
