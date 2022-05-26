package cc.jinhx.easytool.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * SecureUtil单测
 *
 * @author jinhx
 * @since 2022-05-06
 */
@Slf4j
public class SecureUtilTest {

    @Test
    public void encoderByBcryptTest() {
        SecureUtil.encoderByBcrypt(null);
    }

}
