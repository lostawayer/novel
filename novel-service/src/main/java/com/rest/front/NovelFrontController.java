package com.rest.front;

import com.rest.compat.SqlSafeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 书籍前台接口
 */
@RestController
@RequestMapping("/xiaoshuoxinxi")
@Tag(name = "书籍前台接口", description = "书籍列表详情等")
public class NovelFrontController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/categories")
    @Operation(summary = "获取书籍类型列表")
    public Map<String, Object> categories() {
        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(
                "SELECT id, CATEGORY_NAME as name FROM novel_category ORDER BY id");
            return FrontResult.ok(list);
        } catch (Exception e) {
            return FrontResult.ok(new ArrayList<>());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "书籍列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(required = false) String xiaoshuomingcheng,
            @RequestParam(required = false) String xiaoshuoleixing) {
        
        String safeSort = SqlSafeUtil.safeSortField(mapSortField(sort), "id");
        String safeOrder = SqlSafeUtil.safeOrder(order);
        
        StringBuilder sql = new StringBuilder("SELECT id, NOVEL_NAME as xiaoshuomingcheng, CATEGORY_NAME as xiaoshuoleixing, ");
        sql.append("PICTURE as fengmian, DESCRIPTION as jianjie, AUTHOR_NAME as zuozhe, ");
        sql.append("CLICK_TIME as clicknum, PUBLISH_TIME as addtime, ACCOUNT as jiage ");
        sql.append("FROM novel_info WHERE 1=1");
        
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM novel_info WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (xiaoshuomingcheng != null && !xiaoshuomingcheng.isEmpty()) {
            sql.append(" AND NOVEL_NAME LIKE ?");
            countSql.append(" AND NOVEL_NAME LIKE ?");
            params.add("%" + xiaoshuomingcheng + "%");
        }
        if (xiaoshuoleixing != null && !xiaoshuoleixing.isEmpty()) {
            sql.append(" AND CATEGORY_NAME = ?");
            countSql.append(" AND CATEGORY_NAME = ?");
            params.add(xiaoshuoleixing);
        }
        
        Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
        
        sql.append(" ORDER BY ").append(safeSort).append(" ").append(safeOrder);
        sql.append(" LIMIT ? OFFSET ?");
        
        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(limit);
        queryParams.add((page - 1) * limit);
        
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
        
        // 批量查询收藏数
        for (Map<String, Object> item : list) {
            try {
                Integer storeupnum = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_collection WHERE REF_ID = ? AND TABLE_NAME = 'xiaoshuoxinxi'",
                    Integer.class, item.get("id"));
                item.put("storeupnum", storeupnum != null ? storeupnum : 0);
            } catch (Exception e) {
                item.put("storeupnum", 0);
            }
        }
        
        return FrontResult.page(list, total != null ? total : 0);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "书籍详情")
    public Map<String, Object> detail(@PathVariable Long id) {
        try {
            String sql = "SELECT id, NOVEL_NAME as xiaoshuomingcheng, CATEGORY_NAME as xiaoshuoleixing, " +
                    "PICTURE as fengmian, DESCRIPTION as jianjie, AUTHOR_NAME as zuozhe, " +
                    "PUBLISH_TIME as addtime, ACCOUNT as jiage " +
                    "FROM novel_info WHERE id = ?";
            Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);
            
            // 查询收藏数作为点击量显示
            try {
                Integer clicknum = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_collection WHERE REF_ID = ? AND TABLE_NAME = 'xiaoshuoxinxi'",
                    Integer.class, id);
                data.put("clicknum", clicknum != null ? clicknum : 0);
            } catch (Exception ex) {
                data.put("clicknum", 0);
            }
            
            return FrontResult.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("书籍不存在: " + e.getMessage());
        }
    }

    @GetMapping("/chapters/{novelId}")
    @Operation(summary = "章节列表")
    public Map<String, Object> chapters(@PathVariable Long novelId) {
        try {
            String sql = "SELECT id, CHAPTER_NUM as zhangjiebianhao, CHAPTER_TITLE as zhangjiemingcheng, " +
                    "VIP_READ as vipRead, ADD_TIME as addtime " +
                    "FROM novel_chapter WHERE REF_ID = ? ORDER BY CHAPTER_NUM ASC";
            List<Map<String, Object>> chapters = jdbcTemplate.queryForList(sql, novelId);
            return FrontResult.ok(chapters);
        } catch (Exception e) {
            return FrontResult.error("获取章节失败");
        }
    }

    @GetMapping("/chapter/{chapterId}")
    @Operation(summary = "章节内容")
    public Map<String, Object> chapter(
            @PathVariable Long chapterId,
            @RequestParam(required = false) Long userId) {
        try {
            String sql = "SELECT c.id, c.CHAPTER_NUM as zhangjiebianhao, c.CHAPTER_TITLE as zhangjiemingcheng, " +
                    "c.CONTENT as neirong, c.VIP_READ as vipRead, c.REF_ID as novelId, " +
                    "n.NOVEL_NAME as xiaoshuomingcheng " +
                    "FROM novel_chapter c " +
                    "LEFT JOIN novel_info n ON c.REF_ID = n.id " +
                    "WHERE c.id = ?";
            Map<String, Object> chapter = jdbcTemplate.queryForMap(sql, chapterId);
            
            // VIP章节权限检查
            if ("是".equals(chapter.get("vipRead"))) {
                boolean isVip = checkUserVip(userId);
                if (!isVip) {
                    chapter.put("neirong", null);
                    chapter.put("needVip", true);
                    chapter.put("vipMsg", "本章节为VIP专享内容，请先购买会员后阅读");
                } else {
                    chapter.put("needVip", false);
                }
            } else {
                chapter.put("needVip", false);
            }
            
            return FrontResult.ok(chapter);
        } catch (Exception e) {
            return FrontResult.error("章节不存在");
        }
    }

    @GetMapping("/comments/{novelId}")
    @Operation(summary = "评论列表")
    public Map<String, Object> comments(
            @PathVariable Long novelId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            String sql = "SELECT id, USER_ID as userid, NICKNAME as nickname, AVATAR_URL as avatarurl, " +
                    "CONTENT as content, REPLY as reply, ADD_TIME as addtime " +
                    "FROM novel_comment WHERE REF_ID = ? ORDER BY ADD_TIME DESC LIMIT ? OFFSET ?";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, novelId, limit, (page - 1) * limit);
            
            Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM novel_comment WHERE REF_ID = ?", Integer.class, novelId);
            
            return FrontResult.page(list, total != null ? total : 0);
        } catch (Exception e) {
            return FrontResult.page(new ArrayList<>(), 0);
        }
    }

    @PostMapping("/comment/add")
    @Operation(summary = "添加评论")
    public Map<String, Object> addComment(@RequestBody Map<String, Object> params) {
        if (params.get("refid") == null || params.get("userid") == null) {
            return FrontResult.error("参数不完整");
        }
        
        String content = (String) params.get("content");
        if (content == null || content.trim().isEmpty()) {
            return FrontResult.error("评论内容不能为空");
        }
        
        try {
            jdbcTemplate.update(
                "INSERT INTO novel_comment (REF_ID, USER_ID, NICKNAME, AVATAR_URL, CONTENT, ADD_TIME) VALUES (?, ?, ?, ?, ?, NOW())",
                params.get("refid"), params.get("userid"), params.get("nickname"), 
                params.get("avatarurl"), content.trim()
            );
            return FrontResult.ok("评论成功");
        } catch (Exception e) {
            return FrontResult.error("评论失败");
        }
    }

    private boolean checkUserVip(Long userId) {
        if (userId == null) return false;
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap("SELECT VIP FROM reader WHERE id = ?", userId);
            return "是".equals(user.get("VIP"));
        } catch (Exception e) {
            return false;
        }
    }

    private String mapSortField(String sort) {
        return switch (sort) {
            case "addtime" -> "PUBLISH_TIME";
            case "clicknum" -> "CLICK_TIME";
            case "xiaoshuomingcheng" -> "NOVEL_NAME";
            default -> "id";
        };
    }
}
