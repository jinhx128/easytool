package cc.jinhx.easytool.process.context;

import lombok.Data;

/**
 * TestContext
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Data
public class TestContext {

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 入参 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 入参 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    private String req;

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 中间数据 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private String A;

    private String B;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 中间数据 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 结果 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private String C1;

    private String C2;

    private String D;

    private String E;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 结果 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}
