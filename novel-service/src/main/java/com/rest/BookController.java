package com.rest;

import com.commons.mvc.WebResult;
import com.commons.data.DataPage;
import com.domain.Book;
import com.service.IBookManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 书籍管理Controller
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@RestController
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/novel/book")
@Tag(name = "书籍管理", description = "书籍管理API")
public class BookController {

    @Autowired
    @Qualifier(IBookManager.ID)
    private IBookManager bookManager;

    @PostMapping("/add")
    @Operation(summary = "添加书籍", description = "添加或更新书籍信息")
    public WebResult<Void> addBook(@RequestBody Book book) {
        bookManager.addOrUpdateBook(book);
        return WebResult.createSuccessWebResult();
    }

    @GetMapping("/get")
    @Operation(summary = "获取书籍", description = "根据ID获取书籍信息")
    public WebResult<Book> getBook(@RequestParam(value = "id") @Parameter(description = "书籍ID") Long id) {
        Book book = bookManager.getBookById(id);
        if (book == null) {
            return WebResult.createFailureWebresult("书籍不存在");
        }
        return WebResult.createSuccessWebResult(book);
    }

    @GetMapping("/list")
    @Operation(summary = "查询书籍列表", description = "分页查询书籍列表")
    public WebResult<DataPage<Book>> findBooks(
            @RequestParam(value = "pageNumber", defaultValue = "1") @Parameter(description = "页码") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Parameter(description = "每页数量") int pageSize,
            @RequestParam(value = "categoryName", required = false) @Parameter(description = "类型名称") String categoryName,
            @RequestParam(value = "authorAccount", required = false) @Parameter(description = "作者账号") String authorAccount) {

        DataPage<Book> books = bookManager.findBooks(pageNumber, pageSize, categoryName, authorAccount);
        return WebResult.createSuccessWebResult(books);
    }

    @GetMapping("/listByAuthor")
    @Operation(summary = "查询作者的书籍", description = "根据作者账号查询书籍列表")
    public WebResult<List<Book>> findBooksByAuthor(
            @RequestParam(value = "authorAccount") @Parameter(description = "作者账号") String authorAccount) {
        List<Book> books = bookManager.findBooksByAuthor(authorAccount);
        return WebResult.createSuccessWebResult(books);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除书籍", description = "根据ID数组删除书籍")
    public WebResult<Void> deleteBook(@RequestParam(value = "ids") @Parameter(description = "书籍ID数组") Long[] ids) {
        bookManager.deleteBook(ids);
        return WebResult.createSuccessWebResult();
    }
}
