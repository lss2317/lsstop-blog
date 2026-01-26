package com.lsstop.mapper;

import com.lsstop.domain.vo.ArticleArchiveVO;
import com.lsstop.domain.vo.ArticleListVO;
import com.lsstop.domain.vo.ArticleSimpleVO;
import com.lsstop.domain.vo.ArticleVO;
import com.lsstop.domain.vo.ArticleViewCountVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章数据访问层
 *
 * @author lishusheng
 * @date 2026/01/18
 */
public interface ArticleMapper {

    /**
     * 获取文章归档列表
     *
     * @return 文章归档列表
     */
    List<ArticleArchiveVO> getArchiveList();

    /**
     * 根据分类ID获取文章列表
     *
     * @param categoryId 分类ID
     * @return 文章列表
     */
    List<ArticleListVO> getArticleListByCategory(Integer categoryId);

    /**
     * 根据标签ID获取文章列表
     *
     * @param tagId 标签ID
     * @return 文章列表
     */
    List<ArticleListVO> getArticleListByTag(Integer tagId);

    /**
     * 根据ID获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleById(Integer id);

    /**
     * 获取上一篇文章
     *
     * @param createTime 当前文章创建时间
     * @return 上一篇文章
     */
    ArticleSimpleVO getPreArticle(LocalDateTime createTime);

    /**
     * 获取下一篇文章
     *
     * @param createTime 当前文章创建时间
     * @return 下一篇文章
     */
    ArticleSimpleVO getNextArticle(LocalDateTime createTime);

    /**
     * 获取最新文章列表
     *
     * @param limit 数量限制
     * @return 最新文章列表
     */
    List<ArticleSimpleVO> getNewestArticles(Integer limit);

    /**
     * 获取推荐文章列表（同分类下的文章）
     *
     * @param categoryId 分类ID
     * @param excludeId  排除的文章ID
     * @param limit      数量限制
     * @return 推荐文章列表
     */
    List<ArticleSimpleVO> getRecommendArticles(@Param("categoryId") Integer categoryId,
                                               @Param("excludeId") Integer excludeId,
                                               @Param("limit") Integer limit);

    /**
     * 获取热门文章列表（按浏览量排序）
     *
     * @param excludeIds 排除的文章ID列表
     * @param limit      数量限制
     * @return 热门文章列表
     */
    List<ArticleSimpleVO> getHotArticles(@Param("excludeIds") List<Integer> excludeIds,
                                         @Param("limit") Integer limit);

    /**
     * 查询所有文章的访问量（用于启动时初始化到Redis）
     *
     * @return 文章访问量列表
     */
    List<ArticleViewCountVO> listAllArticleViewCounts();

    /**
     * 批量更新文章访问量
     *
     * @param viewCounts 访问量列表
     */
    void batchUpdateViewCounts(@Param("list") List<ArticleViewCountVO> viewCounts);

}
