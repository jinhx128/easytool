package cc.jinhx.easytool.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * JsonUtil单测
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class JsonUtilTest {

    @Test
    public void objectConvertToJsonTest() {
        JsonUtil.objectConvertToJson(null);
    }

}

