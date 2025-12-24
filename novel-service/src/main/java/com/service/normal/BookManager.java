package com.service.normal;

import com.commons.data.DataPage;
import com.domain.Book;
import com.persistence.IBookPersistence;
import com.service.IBookManager;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 书籍管理服务实现
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Service(IBookManager.ID)
@CommonsLog
public class BookManager implements IBookManager {

    @Autowired
    @Qualifier("bookPersistence")
    private IBookPersistence bookPersistence;

    @Override
    @Transactional
    public void addOrUpdateBook(Book book) {
        if (book.getId() == null) {
            // 新增
            book.setCreateTime(new Date());
            book.setPublishTime(new Date());
            book.setClickCount(0);
            bookPersistence.save(book);
            log.info("添加书籍成功：" + book.getBookName());
        } else {
            // 更新
            bookPersistence.update(book);
            log.info("更新书籍成功：" + book.getBookName());
        }
    }

    @Override
    public Book getBookById(Long id) {
        return bookPersistence.findById(id);
    }

    @Override
    public Book getBookByName(String bookName) {
        return bookPersistence.findByName(bookName);
    }

    @Override
    @Transactional
    public void deleteBook(Long[] ids) {
        if (ids != null && ids.length > 0) {
            bookPersistence.deleteByIds(ids);
            log.info("删除书籍数量：" + ids.length);
        }
    }

    @Override
    public DataPage<Book> findBooks(int pageNumber, int pageSize, String categoryName, String authorAccount) {
        int offset = (pageNumber - 1) * pageSize;
        List<Book> books = bookPersistence.findPage(offset, pageSize, categoryName, authorAccount);
        long total = bookPersistence.count(categoryName, authorAccount);

        return DataPage.createPage(books, pageNumber, pageSize, total);
    }

    @Override
    public List<Book> findBooksByAuthor(String authorAccount) {
        return bookPersistence.findByAuthor(authorAccount);
    }

    @Override
    @Transactional
    public void updateClickCount(Long bookId) {
        bookPersistence.updateClickCount(bookId);
    }
}
