package com.commons.security.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import freemarker.template.utility.DateUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class JwtUtil {
    //加密
    public static String encodeJwt(String applicationName,String username,String password,int expireSecond){
        Algorithm algorithm = Algorithm.HMAC256(password);
        JWTCreator.Builder builder = JWT.create();
        builder.withHeader(new HashMap<>(){{
            put("typ","JWT");
            put("alg","HS256");
        }});
        builder.withIssuer(applicationName);//签发者
        builder.withSubject(username);//主题
        builder.withAudience(username);//接收方
        Date now = new Date();
        builder.withIssuedAt(now);//签发时间
        Date expire =DateUtils.addSeconds(now,expireSecond);
        builder.withExpiresAt(expire);//过期时间
        builder.withNotBefore(now);//生效时间
        String token = builder.sign(algorithm);
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(token.getBytes());
    }
    public static boolean decodeJwt(String password,String jwt){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes=decoder.decode(jwt);
        String token=new String(bytes);
        Algorithm algorithm = Algorithm.HMAC256(password);
        //JWT.require(algorithm).build();
        JWTVerifier verifier = JWT.require(algorithm).build();
        boolean result=false;
        try{verifier.verify(token);
            result=true;
        }catch (JWTVerificationException e){
            result=false;
        }
        return result;
    }

    public static String decodeJwtSubject(String jwt) {
        String subject;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] bytes=decoder.decode(jwt);
            String token=new String(bytes);
            DecodedJWT decodedJWT=JWT.decode(token);
            subject=decodedJWT.getSubject();
        }catch (Exception e){
            subject=null;
        }
        return subject;
    }
}