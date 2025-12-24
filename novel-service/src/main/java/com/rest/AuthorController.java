package com.rest;

import com.commons.mvc.WebResult;
import com.commons.data.DataPage;
import com.domain.Author;
import com.service.IAuthorManager;
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
 * 作者管理Controller
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@RestController
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@RequestMapping("/novel/author")
@Tag(name = "作者管理", description = "作者管理API")
public class AuthorController {

    @Autowired
    @Qualifier(IAuthorManager.ID)
    private IAuthorManager authorManager;

    @PostMapping("/add")
    @Operation(summary = "添加作者", description = "添加或更新作者信息")
    public WebResult<Void> addAuthor(@RequestBody Author author) {
        authorManager.addOrUpdateAuthor(author);
        return WebResult.createSuccessWebResult();
    }

    @GetMapping("/get")
    @Operation(summary = "获取作者", description = "根据账号获取作者信息")
    public WebResult<Author> getAuthor(
            @RequestParam(value = "account") @Parameter(description = "账号") String account) {
        Author author = authorManager.getAuthorByAccount(account);
        if (author == null) {
            return WebResult.createFailureWebresult("作者不存在");
        }
        // 隐藏密码
        author.setPassword(null);
        return WebResult.createSuccessWebResult(author);
    }

    @GetMapping("/list")
    @Operation(summary = "查询作者列表", description = "分页查询作者列表")
    public WebResult<DataPage<Author>> findAuthors(
            @RequestParam(value = "pageNumber", defaultValue = "1") @Parameter(description = "页码") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") @Parameter(description = "每页数量") int pageSize) {
        DataPage<Author> authors = authorManager.findAuthors(pageNumber, pageSize);
        // 隐藏密码
        authors.getData().forEach(author -> author.setPassword(null));
        return WebResult.createSuccessWebResult(authors);
    }

    @GetMapping("/exists")
    @Operation(summary = "检查账号", description = "检查账号是否存在")
    public WebResult<Boolean> isAccountExist(
            @RequestParam(value = "account") @Parameter(description = "账号") String account) {
        boolean exists = authorManager.isAccountExist(account);
        return WebResult.createSuccessWebResult(exists);
    }

    @PostMapping("/login")
    @Operation(summary = "作者登录", description = "作者登录验证")
    public WebResult<Author> login(
            @RequestParam(value = "account") @Parameter(description = "账号") String account,
            @RequestParam(value = "password") @Parameter(description = "密码") String password) {
        // 先获取作者信息
        Author author = authorManager.getAuthorForLogin(account);
        if (author == null) {
            return WebResult.createFailureWebresult("账号不存在");
        }
        
        // 验证密码
        if (!password.equals(author.getPassword())) {
            return WebResult.createFailureWebresult("密码错误");
        }
        
        // 检查审核状态
        String auditStatus = author.getAuditStatus();
        if (!"是".equals(auditStatus)) {
            if ("待审核".equals(auditStatus)) {
                return WebResult.createFailureWebresult("您的账号正在审核中，请耐心等待管理员审核");
            } else {
                String auditReply = author.getAuditReply();
                String msg = "账号审核未通过";
                if (auditReply != null && !auditReply.isEmpty()) {
                    msg += "，原因：" + auditReply;
                }
                return WebResult.createFailureWebresult(msg);
            }
        }
        
        // 隐藏密码
        author.setPassword(null);
        return WebResult.createSuccessWebResult(author);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除作者", description = "根据账号数组删除作者")
    public WebResult<Void> deleteAuthor(
            @RequestParam(value = "accounts") @Parameter(description = "账号数组") String[] accounts) {
        authorManager.deleteAuthor(accounts);
        return WebResult.createSuccessWebResult();
    }
}
