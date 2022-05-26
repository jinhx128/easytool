package cc.jinhx.easytool.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * MapUtil单测
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class MapUtilTest {

    @Test
    public void mapToObjectTest() {
        MapUtil.mapToObject(null, null);
    }

}

