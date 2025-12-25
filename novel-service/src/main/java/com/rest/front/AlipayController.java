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
                
                // 更新订单状态
                jdbcTemplate.update(
                    "UPDATE vip_order SET STATUS = 'PAID', PAY_TIME = ? WHERE ORDER_NO = ?",
                    LocalDateTime.now(), out_trade_no
                );
                
                // 更新用户VIP状态
                jdbcTemplate.update("UPDATE reader SET VIP = '是' WHERE id = ?", userId);
            }
            
            return FrontResult.ok("支付成功");
        } catch (Exception e) {
            e.printStackTrace();
            return FrontResult.error("处理失败");
        }
    }
    
    private boolean queryAlipayAndUpdate(String orderNo) {
        // 简化处理：沙箱环境直接返回false，依赖return回调
        return false;
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
