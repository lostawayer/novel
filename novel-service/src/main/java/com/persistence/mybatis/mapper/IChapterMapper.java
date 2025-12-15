package com.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 章节Mapper
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Mapper
public interface IChapterMapper extends BaseMapper<Chapter> {

    /**
     * 分页查询
     */
    List<Chapter> selectPage(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("bookId") Long bookId,
            @Param("authorAccount") String authorAccount);

    /**
     * 统计数量
     */
    long selectCount(@Param("bookId") Long bookId,
            @Param("authorAccount") String authorAccount);

    /**
     * 根据小说ID查询所有章节
     */
    List<Chapter> selectByBookId(@Param("bookId") Long bookId);
}
