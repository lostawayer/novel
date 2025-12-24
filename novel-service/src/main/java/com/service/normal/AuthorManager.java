package com.service.normal;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.commons.data.DataPage;
import com.domain.Author;
import com.persistence.mybatis.mapper.IAuthorMapper;
import com.service.IAuthorManager;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 作者管理服务实现
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Service(IAuthorManager.ID)
@CommonsLog
public class AuthorManager implements IAuthorManager {

    @Autowired
    private IAuthorMapper authorMapper;

    @Override
    @Transactional
    public void addOrUpdateAuthor(Author author) {
        if (author.getId() == null) {
            // 新增 - 设置默认审核状态为待审核
            author.setCreateTime(new Date());
            if (author.getAuditStatus() == null || author.getAuditStatus().isEmpty()) {
                author.setAuditStatus("待审核");
            }
            authorMapper.insert(author);
            log.info("添加作者成功：" + author.getAccount());
        } else {
            // 更新 - 如果密码为空，先获取原密码
            if (author.getPassword() == null || author.getPassword().isEmpty()) {
                Author existingAuthor = authorMapper.selectById(author.getId());
                if (existingAuthor != null) {
                    author.setPassword(existingAuthor.getPassword());
                }
            }
            authorMapper.updateById(author);
            log.info("更新作者成功：" + author.getAccount());
        }
    }

    @Override
    public Author getAuthorByAccount(String account) {
        return authorMapper.selectByAccount(account);
    }

    @Override
    public boolean isAccountExist(String account) {
        Author author = authorMapper.selectByAccount(account);
        return author != null;
    }

    @Override
    @Transactional
    public void deleteAuthor(String[] accounts) {
        if (accounts != null && accounts.length > 0) {
            for (String account : accounts) {
                QueryWrapper<Author> wrapper = new QueryWrapper<>();
                wrapper.eq("ACCOUNT", account);
                authorMapper.delete(wrapper);
            }
            log.info("删除作者数量：" + accounts.length);
        }
    }

    @Override
    public DataPage<Author> findAuthors(int pageNumber, int pageSize) {
        Page<Author> page = new Page<>(pageNumber, pageSize);
        Page<Author> result = authorMapper.selectPage(page, null);

        return DataPage.createPage(result.getRecords(), pageNumber, pageSize, result.getTotal());
    }

    @Override
    public Author login(String account, String password) {
        Author author = authorMapper.selectByAccount(account);
        if (author == null) {
            return null;
        }
        if (!password.equals(author.getPassword())) {
            return null;
        }
        return author;
    }
    
    @Override
    public Author getAuthorForLogin(String account) {
        return authorMapper.selectByAccount(account);
    }
}
