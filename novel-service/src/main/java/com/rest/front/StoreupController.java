package com.rest.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 收藏接口
 */
@RestController
@RequestMapping("/storeup")
@Tag(name = "收藏接口", description = "收藏功能")
public class StoreupController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "收藏列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long userid,
            @RequestParam(required = false) Long refid,
            @RequestParam(required = false) String tablename) {
        
        StringBuilder sql = new StringBuilder("SELECT id, USER_ID as userid, REF_ID as refid, ");
        sql.append("TABLE_NAME as tablename, NAME as name, PICTURE as picture, ADD_TIME as addtime ");
        sql.append("FROM user_collection WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM user_collection WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (userid != null) {
            sql.append(" AND USER_ID = ?");
            countSql.append(" AND USER_ID = ?");
            params.add(userid);
        }
        if (refid != null) {
            sql.append(" AND REF_ID = ?");
            countSql.append(" AND REF_ID = ?");
            params.add(refid);
        }
        if (tablename != null && !tablename.isEmpty()) {
            sql.append(" AND TABLE_NAME = ?");
            countSql.append(" AND TABLE_NAME = ?");
            params.add(tablename);
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return FrontResult.page(list, total != null ? total : 0);
    }


    @PostMapping("/add")
    @Operation(summary = "添加收藏")
    public Map<String, Object> add(@RequestBody Map<String, Object> params) {
        if (params.get("userid") == null || params.get("refid") == null) {
            return FrontResult.error("参数不完整");
        }
        
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_collection WHERE USER_ID = ? AND REF_ID = ? AND TABLE_NAME = ?",
                Integer.class, params.get("userid"), params.get("refid"), params.get("tablename"));
            
            if (count != null && count > 0) {
                return FrontResult.error("已收藏");
            }
            
            jdbcTemplate.update(
                "INSERT INTO user_collection (USER_ID, REF_ID, TABLE_NAME, NAME, PICTURE, ADD_TIME) VALUES (?, ?, ?, ?, ?, ?)",
                params.get("userid"), params.get("refid"), params.get("tablename"),
                params.get("name"), params.get("picture"), LocalDateTime.now());
            
            return FrontResult.ok("收藏成功");
        } catch (Exception e) {
            return FrontResult.error("收藏失败");
        }
    }

    @PostMapping("/delete")
    @Operation(summary = "取消收藏")
    public Map<String, Object> delete(@RequestBody Map<String, Object> params) {
        if (params.get("userid") == null || params.get("refid") == null) {
            return FrontResult.error("参数不完整");
        }
        
        try {
            jdbcTemplate.update(
                "DELETE FROM user_collection WHERE USER_ID = ? AND REF_ID = ? AND TABLE_NAME = ?",
                params.get("userid"), params.get("refid"), params.get("tablename"));
            return FrontResult.ok("取消收藏成功");
        } catch (Exception e) {
            return FrontResult.error("操作失败");
        }
    }
}
