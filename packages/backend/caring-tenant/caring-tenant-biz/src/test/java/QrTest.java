import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * 测试bcrypt加密算法
 */
public class QrTest {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static void main(String[] args) {
        String password = "123456";
        String encodePassword1 = DigestUtil.bcrypt(password);
        System.out.println(encodePassword1);
        System.out.println(DigestUtil.bcryptCheck(password, encodePassword1));


        String encodePassword = passwordEncoder.encode(password);
        boolean match = passwordEncoder.matches(password, encodePassword);
        System.out.println(encodePassword);
        System.out.println(match);
    }
}
