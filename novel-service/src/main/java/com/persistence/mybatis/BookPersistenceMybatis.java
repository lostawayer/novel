package com.persistence.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.domain.Book;
import com.persistence.IBookPersistence;
import com.persistence.mybatis.mapper.IBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 书籍持久化MyBatis实现
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Repository("bookPersistence")
public class BookPersistenceMybatis implements IBookPersistence {

    @Autowired
    private IBookMapper bookMapper;

    @Override
    public void save(Book book) {
        bookMapper.insert(book);
    }

    @Override
    public void update(Book book) {
        bookMapper.updateById(book);
    }

    @Override
    public void deleteById(Long id) {
        bookMapper.deleteById(id);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        for (Long id : ids) {
            bookMapper.deleteById(id);
        }
    }

    @Override
    public Book findById(Long id) {
        return bookMapper.selectById(id);
    }

    @Override
    public Book findByName(String bookName) {
        QueryWrapper<Book> wrapper = new QueryWrapper<>();
        wrapper.eq("NOVEL_NAME", bookName);
        return bookMapper.selectOne(wrapper);
    }

    @Override
    public List<Book> findAll() {
        return bookMapper.selectList(null);
    }

    @Override
    public List<Book> findPage(int offset, int limit, String categoryName, String authorAccount) {
        return bookMapper.selectPage(offset, limit, categoryName, authorAccount);
    }

    @Override
    public long count(String categoryName, String authorAccount) {
        return bookMapper.selectCount(categoryName, authorAccount);
    }

    @Override
    public List<Book> findByAuthor(String authorAccount) {
        return bookMapper.selectByAuthor(authorAccount);
    }

    @Override
    public void updateClickCount(Long bookId) {
        bookMapper.updateClickCount(bookId);
    }
}
