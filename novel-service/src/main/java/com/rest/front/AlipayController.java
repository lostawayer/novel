package com.rest.front;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝支付接口
 */
@RestController
@RequestMapping("/alipay")
@Tag(name = "支付宝支付", description = "VIP会员支付接口")
public class AlipayController {

    @Autowired
    private AlipayConfig alipayConfig;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> params) {
        Long userId = params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : null;
        String vipType = (String) params.getOrDefault("vipType", "month");
        
        if (userId == null) {
            return FrontResult.error("用户ID不能为空");
        }
        
        // 根据VIP类型确定价格
        double amount;
        String subject;
        int days;
        switch (vipType) {
            case "quarter":
                amount = 40.00;
                subject = "文趣阁VIP季度会员";
                days = 90;
                break;
            case "year":
                amount = 128.00;
                subject = "文趣阁VIP年度会员";
                days = 365;
                break;
            default:
                amount = 15.00;
                subject = "文趣阁VIP月度会员";
                days = 30;
        }
        
        // 生成订单号
        String outTradeNo = "VIP" + userId + "_" + System.currentTimeMillis();
        
        try {
            // 保存订单到数据库
            jdbcTemplate.update(
                "INSERT INTO vip_order (ORDER_NO, USER_ID, VIP_TYPE, AMOUNT, DAYS, STATUS, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?, ?)",
                outTradeNo, userId, vipType, amount, days, "PENDING", LocalDateTime.now()
            );
            
            // 创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
            );

            // 创建支付请求
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(alipayConfig.getReturnUrl());
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            
            // 设置业务参数
            String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%.2f\",\"subject\":\"%s\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                outTradeNo, amount, subject
            );
            request.setBizContent(bizContent);
            
            // 获取支付表单HTML
            String form = alipayClient.pageExecute(request).getBody();
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderNo", outTradeNo);
            data.put("payForm", form);
            data.put("amount", amount);
            
            return FrontResult.ok("订单创建成功", data);
            
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return FrontResult.error("创建支付订单失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("系统错误");
        }
    }

    /**
     * 支付宝异步通知回调
     */
    @PostMapping("/notify")
    @Operation(summary = "支付宝异步通知")
    public String notify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        
        try {
            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(),
                alipayConfig.getSignType()
            );
            
            if (signVerified) {
                String tradeStatus = params.get("trade_status");
                String outTradeNo = params.get("out_trade_no");
                String tradeNo = params.get("trade_no");
                
                // 支付成功
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    processPaymentSuccess(outTradeNo, tradeNo);
                }
                return "success";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "failure";
    }

    /**
     * 查询订单状态并更新VIP（用于同步回调）
     */
    @GetMapping("/query")
    @Operation(summary = "查询订单状态")
    public Map<String, Object> queryOrder(@RequestParam String orderNo) {
        try {
            Map<String, Object> order = jdbcTemplate.queryForMap(
                "SELECT * FROM vip_order WHERE ORDER_NO = ?", orderNo
            );
            
            // 如果订单还是PENDING状态，主动查询支付宝并更新
            if ("PENDING".equals(order.get("STATUS"))) {
                boolean paid = queryAlipayAndUpdate(orderNo);
                if (paid) {
                    order.put("STATUS", "PAID");
                }
            }
            
            return FrontResult.ok(order);
        } catch (Exception e) {
            return FrontResult.error("订单不存在");
        }
    }
    
    /**
     * 支付成功回调 - 同步更新VIP状态
     */
    @GetMapping("/return")
    @Operation(summary = "支付成功同步回调")
    public Map<String, Object> returnCallback(@RequestParam String out_trade_no) {
        try {
            // 查询订单
            Map<String, Object> order = jdbcTemplate.queryForMap(
                "SELECT * FROM vip_order WHERE ORDER_NO = ?", out_trade_no
            );
            
            // 如果还未处理，直接更新VIP状态
            if ("PENDING".equals(order.get("STATUS"))) {
                Long userId = ((Number) order.get("USER_ID")).longValue();
                Integer days = ((Number) order.get("DAYS")).intValue();
                
                // 更新订单状态
                jdbcTemplate.update(
                    "UPDATE vip_order SET STATUS = 'PAID', PAY_TIME = ? WHERE ORDER_NO = ?",
                    LocalDateTime.now(), out_trade_no
                );
                
                // 查询用户当前VIP到期时间
                Map<String, Object> user = jdbcTemplate.queryForMap(
                    "SELECT VIP, VIP_EXPIRE_TIME FROM reader WHERE id = ?", userId
                );
                
                LocalDateTime newExpireTime;
                Object expireTimeObj = user.get("VIP_EXPIRE_TIME");
                
                if ("是".equals(user.get("VIP")) && expireTimeObj != null) {
                    // 已是VIP，在原到期时间基础上续期
                    LocalDateTime currentExpire;
                    if (expireTimeObj instanceof java.sql.Timestamp) {
                        currentExpire = ((java.sql.Timestamp) expireTimeObj).toLocalDateTime();
                    } else {
                        currentExpire = (LocalDateTime) expireTimeObj;
                    }
                    // 如果还没过期，从到期时间开始加；如果已过期，从现在开始加
                    if (currentExpire.isAfter(LocalDateTime.now())) {
                        newExpireTime = currentExpire.plusDays(days);
                    } else {
                        newExpireTime = LocalDateTime.now().plusDays(days);
                    }
                } else {
                    // 新开通VIP，从现在开始计算
                    newExpireTime = LocalDateTime.now().plusDays(days);
                }
                
                // 更新用户VIP状态和到期时间
                jdbcTemplate.update(
                    "UPDATE reader SET VIP = '是', VIP_EXPIRE_TIME = ? WHERE id = ?",
                    newExpireTime, userId
                );
            }
            
            return FrontResult.ok("支付成功");
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("处理失败");
        }
    }
    
    /**
     * 获取用户订单列表（包含VIP订单和书籍购买订单）
     */
    @GetMapping("/orders")
    @Operation(summary = "获取用户订单列表")
    public Map<String, Object> getOrders(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            int offset = (page - 1) * limit;
            
            // 使用UNION合并VIP订单和书籍购买订单
            String sql = "SELECT * FROM (" +
                "SELECT ORDER_NO as orderNo, 'VIP' as orderType, VIP_TYPE as vipType, AMOUNT as amount, " +
                "DAYS as days, STATUS as status, CREATE_TIME as createTime, PAY_TIME as payTime, NULL as bookId, NULL as bookName " +
                "FROM vip_order WHERE USER_ID = ? " +
                "UNION ALL " +
                "SELECT p.ORDER_NO as orderNo, 'BOOK' as orderType, NULL as vipType, p.AMOUNT as amount, " +
                "NULL as days, p.STATUS as status, p.CREATE_TIME as createTime, p.PAY_TIME as payTime, p.BOOK_ID as bookId, n.NOVEL_NAME as bookName " +
                "FROM user_book_purchase p LEFT JOIN novel_info n ON p.BOOK_ID = n.id WHERE p.USER_ID = ? " +
                ") t ORDER BY createTime DESC LIMIT ? OFFSET ?";
            
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(sql, userId, userId, limit, offset);
            
            // 统计总数
            String countSql = "SELECT (" +
                "(SELECT COUNT(*) FROM vip_order WHERE USER_ID = ?) + " +
                "(SELECT COUNT(*) FROM user_book_purchase WHERE USER_ID = ?)" +
                ") as total";
            Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, userId, userId);
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", orders);
            data.put("total", total != null ? total : 0);
            
            return FrontResult.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("查询失败");
        }
    }
    
    private boolean queryAlipayAndUpdate(String orderNo) {
        // 简化处理：沙箱环境直接返回false，依赖return回调
        return false;
    }
    
    /**
     * 创建购买书籍订单
     */
    @PostMapping("/createBookOrder")
    @Operation(summary = "创建购买书籍订单")
    public Map<String, Object> createBookOrder(@RequestBody Map<String, Object> params) {
        Long userId = params.get("userId") != null ? Long.valueOf(params.get("userId").toString()) : null;
        Long bookId = params.get("bookId") != null ? Long.valueOf(params.get("bookId").toString()) : null;
        
        if (userId == null || bookId == null) {
            return FrontResult.error("参数不完整");
        }
        
        try {
            // 检查是否已购买
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_book_purchase WHERE USER_ID = ? AND BOOK_ID = ? AND STATUS = 'PAID'",
                Integer.class, userId, bookId
            );
            if (count != null && count > 0) {
                return FrontResult.error("您已购买过此书籍");
            }
            
            // 获取书籍信息
            Map<String, Object> book = jdbcTemplate.queryForMap(
                "SELECT NOVEL_NAME, PRICE FROM novel_info WHERE id = ?", bookId
            );
            
            String bookName = (String) book.get("NOVEL_NAME");
            Object priceObj = book.get("PRICE");
            double amount = 0;
            if (priceObj != null) {
                amount = Double.parseDouble(priceObj.toString());
            }
            
            if (amount <= 0) {
                return FrontResult.error("该书籍为免费书籍，无需购买");
            }
            
            // 生成订单号
            String outTradeNo = "BOOK" + userId + "_" + bookId + "_" + System.currentTimeMillis();
            
            // 保存订单
            jdbcTemplate.update(
                "INSERT INTO user_book_purchase (USER_ID, BOOK_ID, ORDER_NO, AMOUNT, STATUS, CREATE_TIME) VALUES (?, ?, ?, ?, ?, ?)",
                userId, bookId, outTradeNo, amount, "PENDING", LocalDateTime.now()
            );
            
            // 创建支付宝订单
            AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                alipayConfig.getCharset(),
                alipayConfig.getAlipayPublicKey(),
                alipayConfig.getSignType()
            );
            
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            // 书籍购买回调到书籍详情页
            String returnUrl = "http://localhost:8080/index/xiaoshuoxinxiDetail?id=" + bookId + "&buyResult=success&out_trade_no=" + outTradeNo;
            request.setReturnUrl(returnUrl);
            request.setNotifyUrl(alipayConfig.getNotifyUrl());
            
            String bizContent = String.format(
                "{\"out_trade_no\":\"%s\",\"total_amount\":\"%.2f\",\"subject\":\"购买书籍《%s》\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}",
                outTradeNo, amount, bookName
            );
            request.setBizContent(bizContent);
            
            String form = alipayClient.pageExecute(request).getBody();
            
            Map<String, Object> data = new HashMap<>();
            data.put("orderNo", outTradeNo);
            data.put("payForm", form);
            data.put("amount", amount);
            data.put("bookName", bookName);
            
            return FrontResult.ok("订单创建成功", data);
            
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("创建订单失败");
        }
    }
    
    /**
     * 书籍购买支付回调
     */
    @GetMapping("/bookReturn")
    @Operation(summary = "书籍购买支付回调")
    public Map<String, Object> bookReturnCallback(@RequestParam String out_trade_no) {
        System.out.println("=== 书籍购买回调 ===");
        System.out.println("订单号: " + out_trade_no);
        try {
            // 查询订单
            List<Map<String, Object>> orders = jdbcTemplate.queryForList(
                "SELECT * FROM user_book_purchase WHERE ORDER_NO = ?", out_trade_no
            );
            
            if (orders.isEmpty()) {
                System.out.println("订单不存在: " + out_trade_no);
                return FrontResult.error("订单不存在");
            }
            
            Map<String, Object> order = orders.get(0);
            System.out.println("订单信息: " + order);
            
            // 如果还未处理，更新状态
            if ("PENDING".equals(order.get("STATUS"))) {
                int updated = jdbcTemplate.update(
                    "UPDATE user_book_purchase SET STATUS = 'PAID', PAY_TIME = ? WHERE ORDER_NO = ?",
                    LocalDateTime.now(), out_trade_no
                );
                System.out.println("更新订单状态，影响行数: " + updated);
            } else {
                System.out.println("订单已处理，状态: " + order.get("STATUS"));
            }
            
            return FrontResult.ok("支付成功");
        } catch (Exception e) {
            System.out.println("处理失败: " + e.getMessage());
            e.printStackTrace();
            return FrontResult.error("处理失败: " + e.getMessage());
        }
    }

    /**
     * 处理支付成功
     */
    private void processPaymentSuccess(String orderNo, String tradeNo) {
        try {
            // 查询订单
            Map<String, Object> order = jdbcTemplate.queryForMap(
                "SELECT * FROM vip_order WHERE ORDER_NO = ? AND STATUS = 'PENDING'", orderNo
            );
            
            Long userId = ((Number) order.get("USER_ID")).longValue();
            Integer days = ((Number) order.get("DAYS")).intValue();
            
            // 更新订单状态
            jdbcTemplate.update(
                "UPDATE vip_order SET STATUS = 'PAID', TRADE_NO = ?, PAY_TIME = ? WHERE ORDER_NO = ?",
                tradeNo, LocalDateTime.now(), orderNo
            );
            
            // 更新用户VIP状态
            jdbcTemplate.update("UPDATE reader SET VIP = '是' WHERE id = ?", userId);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
