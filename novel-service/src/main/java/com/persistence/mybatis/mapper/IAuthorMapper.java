package com.persistence.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.domain.Author;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 作者Mapper
 * 
 * @author Novel Platform Team
 * @date 2024-12-11
 */
@Mapper
public interface IAuthorMapper extends BaseMapper<Author> {

    /**
     * 根据账号查询
     */
    Author selectByAccount(@Param("account") String account);

    /**
     * 登录验证
     */
    Author selectByAccountAndPassword(@Param("account") String account,
            @Param("password") String password);
}
