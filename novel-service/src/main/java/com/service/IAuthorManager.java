package com.service;

import com.commons.data.DataPage;
import com.domain.Author;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 作者管理服务接口
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Validated
public interface IAuthorManager {
    String ID = "authorManager";

    /**
     * 添加或更新作者
     */
    void addOrUpdateAuthor(@Valid Author author);

    /**
     * 根据账号获取作者
     */
    Author getAuthorByAccount(@NotBlank(message = "账号不能为空") String account);

    /**
     * 账号是否存在
     */
    boolean isAccountExist(@NotBlank(message = "账号不能为空") String account);

    /**
     * 删除作者
     */
    void deleteAuthor(String[] accounts);

    /**
     * 分页查询作者
     */
    DataPage<Author> findAuthors(int pageNumber, int pageSize);

    /**
     * 作者登录
     */
    Author login(@NotBlank String account, @NotBlank String password);
}
