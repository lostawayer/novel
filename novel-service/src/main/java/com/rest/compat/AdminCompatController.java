package com.rest.compat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 管理后台兼容接口
 * 为 novel-admin 提供 /api 前缀的接口
 */
@RestController
@RequestMapping("/api")
@Tag(name = "管理后台兼容接口", description = "管理后台API")
public class AdminCompatController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ==================== 作者管理 ====================
    
    @GetMapping("/zuozhe/page")
    @Operation(summary = "分页查询作者")
    public Map<String, Object> authorPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String authorName,
            @RequestParam(required = false) String auditStatus) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id as ID, ACCOUNT, AUTHOR_NAME, GENDER, AVATAR, AGE, ID_CARD, PHONE, EMAIL, AUDIT_STATUS, AUDIT_REPLY, CREATE_TIME FROM author WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM author WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (account != null && !account.isEmpty()) {
            sql.append(" AND ACCOUNT LIKE ?");
            countSql.append(" AND ACCOUNT LIKE ?");
            params.add("%" + account + "%");
        }
        if (authorName != null && !authorName.isEmpty()) {
            sql.append(" AND AUTHOR_NAME LIKE ?");
            countSql.append(" AND AUTHOR_NAME LIKE ?");
            params.add("%" + authorName + "%");
        }
        if (auditStatus != null && !auditStatus.isEmpty()) {
            sql.append(" AND AUDIT_STATUS = ?");
            countSql.append(" AND AUDIT_STATUS = ?");
            params.add(auditStatus);
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/zuozhe/info/{id}")
    @Operation(summary = "获取作者详情")
    public Map<String, Object> authorInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id as ID, ACCOUNT, AUTHOR_NAME, GENDER, AVATAR, AGE, ID_CARD, PHONE, EMAIL, AUDIT_STATUS, AUDIT_REPLY, CREATE_TIME FROM author WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("作者不存在");
        }
    }

    @PostMapping("/zuozhe/save")
    @Operation(summary = "新增作者")
    public Map<String, Object> authorSave(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "INSERT INTO author (ACCOUNT, PASSWORD, AUTHOR_NAME, GENDER, AVATAR, AGE, ID_CARD, PHONE, EMAIL, AUDIT_STATUS, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '待审核', ?)",
                data.get("account"), data.get("password"), data.get("authorName"),
                data.get("gender"), data.get("avatar"), data.get("age"),
                data.get("idCard"), data.get("phone"), data.get("email"), LocalDateTime.now());
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/zuozhe/update")
    @Operation(summary = "更新作者")
    public Map<String, Object> authorUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE author SET AUTHOR_NAME=?, GENDER=?, AVATAR=?, AGE=?, ID_CARD=?, PHONE=?, EMAIL=? WHERE id=?",
                data.get("authorName"), data.get("gender"), data.get("avatar"),
                data.get("age"), data.get("idCard"), data.get("phone"), data.get("email"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/zuozhe/audit")
    @Operation(summary = "审核作者")
    public Map<String, Object> authorAudit(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("UPDATE author SET AUDIT_STATUS=?, AUDIT_REPLY=? WHERE id=?",
                data.get("auditStatus"), data.get("auditReply"), data.get("id"));
            return CompatResult.ok("审核成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/zuozhe/delete")
    @Operation(summary = "删除作者")
    public Map<String, Object> authorDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM author WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }


    // ==================== 书籍管理 ====================
    
    @GetMapping("/xiaoshuoxinxi/page")
    @Operation(summary = "分页查询书籍")
    public Map<String, Object> novelPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String novelName,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String authorName) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, NOVEL_NAME, CATEGORY_NAME, PICTURE, DESCRIPTION, ACCOUNT, AUTHOR_NAME, PUBLISH_TIME, CLICK_TIME, ADD_TIME, PRICE FROM novel_info WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM novel_info WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (novelName != null && !novelName.isEmpty()) {
            sql.append(" AND NOVEL_NAME LIKE ?");
            countSql.append(" AND NOVEL_NAME LIKE ?");
            params.add("%" + novelName + "%");
        }
        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append(" AND CATEGORY_NAME = ?");
            countSql.append(" AND CATEGORY_NAME = ?");
            params.add(categoryName);
        }
        if (authorName != null && !authorName.isEmpty()) {
            sql.append(" AND AUTHOR_NAME LIKE ?");
            countSql.append(" AND AUTHOR_NAME LIKE ?");
            params.add("%" + authorName + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/xiaoshuoxinxi/info/{id}")
    @Operation(summary = "获取书籍详情")
    public Map<String, Object> novelInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, NOVEL_NAME, CATEGORY_NAME, PICTURE, DESCRIPTION, ACCOUNT, AUTHOR_NAME, PUBLISH_TIME, CLICK_TIME, ADD_TIME, PRICE FROM novel_info WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("书籍不存在");
        }
    }

    @PostMapping("/xiaoshuoxinxi/save")
    @Operation(summary = "新增书籍")
    public Map<String, Object> novelSave(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_info (NOVEL_NAME, CATEGORY_NAME, PICTURE, DESCRIPTION, ACCOUNT, AUTHOR_NAME, PUBLISH_TIME) VALUES (?, ?, ?, ?, ?, ?, NOW())",
                data.get("novelName"), data.get("categoryName"), data.get("picture"),
                data.get("description"), data.get("account"), data.get("authorName"));
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/xiaoshuoxinxi/update")
    @Operation(summary = "更新书籍")
    public Map<String, Object> novelUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_info SET NOVEL_NAME=?, CATEGORY_NAME=?, PICTURE=?, DESCRIPTION=?, PRICE=? WHERE id=?",
                data.get("novelName"), data.get("categoryName"), data.get("picture"),
                data.get("description"), data.get("price"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/xiaoshuoxinxi/delete")
    @Operation(summary = "删除书籍")
    public Map<String, Object> novelDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_info WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    // ==================== 书籍类型管理 ====================
    
    @GetMapping("/xiaoshuoleixing/page")
    @Operation(summary = "分页查询类型")
    public Map<String, Object> categoryPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String categoryName) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, CATEGORY_NAME, ADD_TIME FROM novel_category WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM novel_category WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (categoryName != null && !categoryName.isEmpty()) {
            sql.append(" AND CATEGORY_NAME LIKE ?");
            countSql.append(" AND CATEGORY_NAME LIKE ?");
            params.add("%" + categoryName + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/xiaoshuoleixing/info/{id}")
    @Operation(summary = "获取类型详情")
    public Map<String, Object> categoryInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, CATEGORY_NAME, ADD_TIME FROM novel_category WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("类型不存在");
        }
    }

    @PostMapping("/xiaoshuoleixing/save")
    @Operation(summary = "新增类型")
    public Map<String, Object> categorySave(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("INSERT INTO novel_category (CATEGORY_NAME, ADD_TIME) VALUES (?, NOW())",
                data.get("categoryName"));
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/xiaoshuoleixing/update")
    @Operation(summary = "更新类型")
    public Map<String, Object> categoryUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("UPDATE novel_category SET CATEGORY_NAME=? WHERE id=?",
                data.get("categoryName"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/xiaoshuoleixing/delete")
    @Operation(summary = "删除类型")
    public Map<String, Object> categoryDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_category WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }


    // ==================== 章节管理 ====================
    
    @GetMapping("/chapterxiaoshuoxinxi/page")
    @Operation(summary = "分页查询章节")
    public Map<String, Object> chapterPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String chapterTitle) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, REF_ID, CHAPTER_NUM, CHAPTER_TITLE, CONTENT, VIP_READ, ADD_TIME FROM novel_chapter WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM novel_chapter WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (refId != null) {
            sql.append(" AND REF_ID = ?");
            countSql.append(" AND REF_ID = ?");
            params.add(refId);
        }
        if (chapterTitle != null && !chapterTitle.isEmpty()) {
            sql.append(" AND CHAPTER_TITLE LIKE ?");
            countSql.append(" AND CHAPTER_TITLE LIKE ?");
            params.add("%" + chapterTitle + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/chapterxiaoshuoxinxi/info/{id}")
    @Operation(summary = "获取章节详情")
    public Map<String, Object> chapterInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, REF_ID, CHAPTER_NUM, CHAPTER_TITLE, CONTENT, VIP_READ, ADD_TIME FROM novel_chapter WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("章节不存在");
        }
    }

    @PostMapping("/chapterxiaoshuoxinxi/save")
    @Operation(summary = "新增章节")
    public Map<String, Object> chapterSave(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_chapter (REF_ID, CHAPTER_NUM, CHAPTER_TITLE, CONTENT, VIP_READ, ADD_TIME) VALUES (?, ?, ?, ?, ?, NOW())",
                data.get("refId"), data.get("chapterNum"), data.get("chapterTitle"),
                data.get("content"), data.get("vipRead"));
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/chapterxiaoshuoxinxi/update")
    @Operation(summary = "更新章节")
    public Map<String, Object> chapterUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE novel_chapter SET CHAPTER_NUM=?, CHAPTER_TITLE=?, CONTENT=?, VIP_READ=? WHERE id=?",
                data.get("chapterNum"), data.get("chapterTitle"), data.get("content"),
                data.get("vipRead"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/chapterxiaoshuoxinxi/delete")
    @Operation(summary = "删除章节")
    public Map<String, Object> chapterDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_chapter WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    // ==================== 评论管理 ====================
    
    @GetMapping("/discussxiaoshuoxinxi/page")
    @Operation(summary = "分页查询评论")
    public Map<String, Object> commentPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String nickname) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT c.id, c.REF_ID, c.USER_ID, c.AVATAR_URL, c.NICKNAME, c.CONTENT, c.REPLY, c.ADD_TIME, n.NOVEL_NAME FROM novel_comment c LEFT JOIN novel_info n ON c.REF_ID = n.id WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM novel_comment WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (refId != null) {
            sql.append(" AND c.REF_ID = ?");
            countSql.append(" AND REF_ID = ?");
            params.add(refId);
        }
        if (nickname != null && !nickname.isEmpty()) {
            sql.append(" AND c.NICKNAME LIKE ?");
            countSql.append(" AND NICKNAME LIKE ?");
            params.add("%" + nickname + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY c.").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/discussxiaoshuoxinxi/info/{id}")
    @Operation(summary = "获取评论详情")
    public Map<String, Object> commentInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, REF_ID, USER_ID, AVATAR_URL, NICKNAME, CONTENT, REPLY, ADD_TIME FROM novel_comment WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("评论不存在");
        }
    }

    @PostMapping("/discussxiaoshuoxinxi/reply")
    @Operation(summary = "回复评论")
    public Map<String, Object> commentReply(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("UPDATE novel_comment SET REPLY=? WHERE id=?",
                data.get("reply"), data.get("id"));
            return CompatResult.ok("回复成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/discussxiaoshuoxinxi/delete")
    @Operation(summary = "删除评论")
    public Map<String, Object> commentDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM novel_comment WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }


    // ==================== 用户管理 ====================
    
    @GetMapping("/yonghu/page")
    @Operation(summary = "分页查询用户")
    public Map<String, Object> userPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, USERNAME, NICKNAME, REAL_NAME, GENDER, AVATAR, EMAIL, PHONE, VIP, ADD_TIME FROM reader WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM reader WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (username != null && !username.isEmpty()) {
            sql.append(" AND USERNAME LIKE ?");
            countSql.append(" AND USERNAME LIKE ?");
            params.add("%" + username + "%");
        }
        if (realName != null && !realName.isEmpty()) {
            sql.append(" AND REAL_NAME LIKE ?");
            countSql.append(" AND REAL_NAME LIKE ?");
            params.add("%" + realName + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/yonghu/info/{id}")
    @Operation(summary = "获取用户详情")
    public Map<String, Object> userInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, USERNAME, NICKNAME, REAL_NAME, GENDER, AVATAR, EMAIL, PHONE, VIP, ADD_TIME FROM reader WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("用户不存在");
        }
    }

    @PostMapping("/yonghu/save")
    @Operation(summary = "新增用户")
    public Map<String, Object> userSave(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "INSERT INTO reader (USERNAME, PASSWORD, NICKNAME, REAL_NAME, GENDER, AVATAR, EMAIL, PHONE, VIP, ADD_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                data.get("username"), data.get("password"), data.get("nickname"),
                data.get("realName"), data.get("gender"), data.get("avatar"),
                data.get("email"), data.get("phone"), data.get("vip"));
            return CompatResult.ok("保存成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/yonghu/update")
    @Operation(summary = "更新用户")
    public Map<String, Object> userUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update(
                "UPDATE reader SET NICKNAME=?, REAL_NAME=?, GENDER=?, AVATAR=?, EMAIL=?, PHONE=?, VIP=? WHERE id=?",
                data.get("nickname"), data.get("realName"), data.get("gender"),
                data.get("avatar"), data.get("email"), data.get("phone"),
                data.get("vip"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/yonghu/delete")
    @Operation(summary = "删除用户")
    public Map<String, Object> userDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM reader WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    // ==================== 留言管理 ====================
    
    @GetMapping("/messages/page")
    @Operation(summary = "分页查询留言")
    public Map<String, Object> messagePage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String username) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, USER_ID, USERNAME, AVATAR_URL, CONTENT, REPLY, ADD_TIME FROM message_board WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM message_board WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (username != null && !username.isEmpty()) {
            sql.append(" AND USERNAME LIKE ?");
            countSql.append(" AND USERNAME LIKE ?");
            params.add("%" + username + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/messages/info/{id}")
    @Operation(summary = "获取留言详情")
    public Map<String, Object> messageInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, USER_ID, USERNAME, AVATAR_URL, CONTENT, REPLY, ADD_TIME FROM message_board WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("留言不存在");
        }
    }

    @PostMapping("/messages/reply")
    @Operation(summary = "回复留言")
    public Map<String, Object> messageReply(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("UPDATE message_board SET REPLY=? WHERE id=?",
                data.get("reply"), data.get("id"));
            return CompatResult.ok("回复成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    @PostMapping("/messages/delete")
    @Operation(summary = "删除留言")
    public Map<String, Object> messageDelete(@RequestBody List<Long> ids) {
        try {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            jdbcTemplate.update("DELETE FROM message_board WHERE id IN (" + placeholders + ")", ids.toArray());
            return CompatResult.ok("删除成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }


    // ==================== 配置管理 ====================
    
    @GetMapping("/config/page")
    @Operation(summary = "分页查询配置")
    public Map<String, Object> configPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String name) {
        
        String safeSort = SqlSafeUtil.safeSortField(sort, "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, NAME, VALUE FROM system_config WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM system_config WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            sql.append(" AND NAME LIKE ?");
            countSql.append(" AND NAME LIKE ?");
            params.add("%" + name + "%");
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        return CompatResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/config/info/{id}")
    @Operation(summary = "获取配置详情")
    public Map<String, Object> configInfo(@PathVariable Long id) {
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(
                "SELECT id, NAME, VALUE FROM system_config WHERE id = ?", id);
            return CompatResult.ok(data);
        } catch (Exception e) {
            return CompatResult.error("配置不存在");
        }
    }

    @PostMapping("/config/update")
    @Operation(summary = "更新配置")
    public Map<String, Object> configUpdate(@RequestBody Map<String, Object> data) {
        try {
            jdbcTemplate.update("UPDATE system_config SET VALUE=? WHERE id=?",
                data.get("value"), data.get("id"));
            return CompatResult.ok("更新成功");
        } catch (Exception e) {
            return CompatResult.error(e.getMessage());
        }
    }

    // ==================== 选项接口 ====================
    
    @GetMapping("/option/category")
    @Operation(summary = "获取书籍类型选项")
    public Map<String, Object> categoryOptions() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT CATEGORY_NAME as label, CATEGORY_NAME as value FROM novel_category ORDER BY id");
        return CompatResult.options(list);
    }

    @GetMapping("/option/author")
    @Operation(summary = "获取作者选项")
    public Map<String, Object> authorOptions() {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(
            "SELECT AUTHOR_NAME as label, ACCOUNT as value FROM author WHERE AUDIT_STATUS = '是' ORDER BY id");
        return CompatResult.options(list);
    }

    @GetMapping("/option/auditStatus")
    @Operation(summary = "获取审核状态选项")
    public Map<String, Object> auditStatusOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "待审核", "value", "待审核"));
        list.add(Map.of("label", "是", "value", "是"));
        list.add(Map.of("label", "否", "value", "否"));
        return CompatResult.options(list);
    }

    @GetMapping("/option/gender")
    @Operation(summary = "获取性别选项")
    public Map<String, Object> genderOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "男", "value", "男"));
        list.add(Map.of("label", "女", "value", "女"));
        return CompatResult.options(list);
    }

    @GetMapping("/option/vip")
    @Operation(summary = "获取VIP选项")
    public Map<String, Object> vipOptions() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("label", "是", "value", "是"));
        list.add(Map.of("label", "否", "value", "否"));
        return CompatResult.options(list);
    }
}
