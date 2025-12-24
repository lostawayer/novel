package com.service;

import com.commons.data.DataPage;
import com.domain.Book;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 书籍管理服务接口
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Validated
public interface IBookManager {
    String ID = "bookManager";

    /**
     * 添加或更新书籍
     */
    void addOrUpdateBook(@Valid Book book);

    /**
     * 根据ID获取书籍
     */
    Book getBookById(@NotNull(message = "书籍ID不能为空") Long id);

    /**
     * 根据书籍名称获取书籍
     */
    Book getBookByName(@NotBlank(message = "书籍名称不能为空") String bookName);

    /**
     * 删除书籍
     */
    void deleteBook(Long[] ids);

    /**
     * 分页查询书籍
     */
    DataPage<Book> findBooks(int pageNumber, int pageSize, String categoryName, String authorAccount);

    /**
     * 根据作者账号查询书籍列表
     */
    List<Book> findBooksByAuthor(@NotBlank(message = "作者账号不能为空") String authorAccount);

    /**
     * 更新点击次数
     */
    void updateClickCount(@NotNull Long bookId);
}
