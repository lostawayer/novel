package com.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书籍Mapper
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Mapper
public interface IBookMapper extends BaseMapper<Book> {

    /**
     * 分页查询
     */
    List<Book> selectPage(@Param("offset") int offset,
            @Param("limit") int limit,
            @Param("categoryName") String categoryName,
            @Param("authorAccount") String authorAccount);

    /**
     * 统计数量
     */
    long selectCount(@Param("categoryName") String categoryName,
            @Param("authorAccount") String authorAccount);

    /**
     * 根据作者查询
     */
    List<Book> selectByAuthor(@Param("authorAccount") String authorAccount);

    /**
     * 更新点击次数
     */
    void updateClickCount(@Param("bookId") Long bookId);
}
