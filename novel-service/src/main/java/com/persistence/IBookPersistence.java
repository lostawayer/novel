package com.persistence;

import com.domain.Book;

import java.util.List;

/**
 * 小说持久化接口
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
public interface IBookPersistence {

    /**
     * 保存小说
     */
    void save(Book book);

    /**
     * 更新小说
     */
    void update(Book book);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);

    /**
     * 批量删除
     */
    void deleteByIds(Long[] ids);

    /**
     * 根据ID查询
     */
    Book findById(Long id);

    /**
     * 根据名称查询
     */
    Book findByName(String bookName);

    /**
     * 查询所有
     */
    List<Book> findAll();

    /**
     * 分页查询
     */
    List<Book> findPage(int offset, int limit, String categoryName, String authorAccount);

    /**
     * 统计总数
     */
    long count(String categoryName, String authorAccount);

    /**
     * 根据作者查询
     */
    List<Book> findByAuthor(String authorAccount);

    /**
     * 更新点击次数
     */
    void updateClickCount(Long bookId);
}
