package commons.security.util;

import com.commons.security.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


public class TestJWT {
    @Test
    public void testJWT() {
        String applicationName="medical";
        String username="admin";
        String password="admin";
        int expireSecond =36;
        String jwt= JwtUtil.encodeJwt(applicationName,username,password,expireSecond);
        System.out.println(jwt);


    }
    @Test
    public void testJWT2() {
        String jwt="ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SnBjM01pT2lKdFpXUnBZMkZzSWl3aWMzVmlJam9pWVdSdGFXNGlMQ0poZFdRaU9pSmhaRzFwYmlJc0ltbGhkQ0k2TVRjMU9EZzFNVFl4T0N3aVpYaHdJam94TnpVNE9EVXhOalUwTENKdVltWWlPakUzTlRnNE5URTJNVGg5LklXeVp4Zi02cFp5Z2lDaGNfNDJ3dFFqSXBteW1wVFRhSzJ3V3YzS3p2aHM=";
        String password="admin";
        if(JwtUtil.decodeJwt(password,jwt)){
            System.out.println("you");
        }else {
            System.out.println("wu");
        }
    }
}
