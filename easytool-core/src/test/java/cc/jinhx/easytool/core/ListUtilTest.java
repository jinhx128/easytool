package cc.jinhx.easytool.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * ListUtil单测
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
public class ListUtilTest {

    @Test
    public void splitByOutsideListSizeTest() {
        ListUtil.splitByOutsideListSize(null, 1);
    }

}

