package com.rest.front;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    
    /** 应用ID */
    private String appId;
    
    /** 应用私钥 */
    private String privateKey;
    
    /** 支付宝公钥 */
    private String alipayPublicKey;
    
    /** 网关地址 */
    private String gatewayUrl;
    
    /** 同步回调地址 */
    private String returnUrl;
    
    /** 异步通知地址 */
    private String notifyUrl;
    
    /** 签名类型 */
    private String signType = "RSA2";
    
    /** 字符编码 */
    private String charset = "UTF-8";
}
