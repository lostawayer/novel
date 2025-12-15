package com.service;

import com.commons.data.DataPage;
import com.domain.Book;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 小说管理服务接口
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Validated
public interface IBookManager {
    String ID = "bookManager";

    /**
     * 添加或更新小说
     */
    void addOrUpdateBook(@Valid Book book);

    /**
     * 根据ID获取小说
     */
    Book getBookById(@NotNull(message = "小说ID不能为空") Long id);

    /**
     * 根据小说名称获取小说
     */
    Book getBookByName(@NotBlank(message = "小说名称不能为空") String bookName);

    /**
     * 删除小说
     */
    void deleteBook(Long[] ids);

    /**
     * 分页查询小说
     */
    DataPage<Book> findBooks(int pageNumber, int pageSize, String categoryName, String authorAccount);

    /**
     * 根据作者账号查询小说列表
     */
    List<Book> findBooksByAuthor(@NotBlank(message = "作者账号不能为空") String authorAccount);

    /**
     * 更新点击次数
     */
    void updateClickCount(@NotNull Long bookId);
}
