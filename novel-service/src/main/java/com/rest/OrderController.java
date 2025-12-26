package com.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理接口（管理后台）
 */
@RestController
@RequestMapping("/api/order")
@Tag(name = "订单管理", description = "VIP订单管理接口")
public class OrderController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    @Operation(summary = "获取订单列表")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username) {
        
        try {
            // 使用UNION合并VIP订单和书籍购买订单
            String baseSql = "SELECT * FROM (" +
                "SELECT o.id, o.ORDER_NO, o.USER_ID, r.USERNAME, 'VIP' as ORDER_TYPE, o.VIP_TYPE, o.AMOUNT, " +
                "o.DAYS, o.STATUS, o.TRADE_NO, o.CREATE_TIME, o.PAY_TIME, NULL as BOOK_ID, NULL as BOOK_NAME " +
                "FROM vip_order o LEFT JOIN reader r ON o.USER_ID = r.id " +
                "UNION ALL " +
                "SELECT p.id, p.ORDER_NO, p.USER_ID, r.USERNAME, 'BOOK' as ORDER_TYPE, NULL as VIP_TYPE, p.AMOUNT, " +
                "NULL as DAYS, p.STATUS, NULL as TRADE_NO, p.CREATE_TIME, p.PAY_TIME, p.BOOK_ID, n.NOVEL_NAME as BOOK_NAME " +
                "FROM user_book_purchase p LEFT JOIN reader r ON p.USER_ID = r.id LEFT JOIN novel_info n ON p.BOOK_ID = n.id" +
                ") t WHERE 1=1";
            
            StringBuilder sql = new StringBuilder(baseSql);
            StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM (" + baseSql + ") c WHERE 1=1");
            
            List<Object> params = new ArrayList<>();
            List<Object> countParams = new ArrayList<>();
            
            if (orderNo != null && !orderNo.isEmpty()) {
                sql.append(" AND ORDER_NO LIKE ?");
                countSql.append(" AND ORDER_NO LIKE ?");
                params.add("%" + orderNo + "%");
                countParams.add("%" + orderNo + "%");
            }
            
            if (status != null && !status.isEmpty()) {
                sql.append(" AND STATUS = ?");
                countSql.append(" AND STATUS = ?");
                params.add(status);
                countParams.add(status);
            }
            
            if (username != null && !username.isEmpty()) {
                sql.append(" AND USERNAME LIKE ?");
                countSql.append(" AND USERNAME LIKE ?");
                params.add("%" + username + "%");
                countParams.add("%" + username + "%");
            }
            
            // 修正count查询
            String finalCountSql = countSql.toString().replace("WHERE 1=1 WHERE 1=1", "WHERE 1=1");
            Integer total = jdbcTemplate.queryForObject(finalCountSql, Integer.class, countParams.toArray());
            
            sql.append(" ORDER BY CREATE_TIME DESC LIMIT ? OFFSET ?");
            List<Object> queryParams = new ArrayList<>(params);
            queryParams.add(limit);
            queryParams.add((page - 1) * limit);
            
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), queryParams.toArray());
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", data);
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 1);
            result.put("msg", "查询失败: " + e.getMessage());
            return result;
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "订单统计")
    public Map<String, Object> stats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总订单数
            Integer totalOrders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vip_order", Integer.class);
            stats.put("totalOrders", totalOrders);
            
            // 已支付订单数
            Integer paidOrders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vip_order WHERE STATUS = 'PAID'", Integer.class);
            stats.put("paidOrders", paidOrders);
            
            // 总收入
            Double totalAmount = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(AMOUNT), 0) FROM vip_order WHERE STATUS = 'PAID'", Double.class);
            stats.put("totalAmount", totalAmount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 0);
            result.put("data", stats);
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 1);
            result.put("msg", "查询失败");
            return result;
        }
    }
}
