package cc.jinhx.easytool.process.test.context;

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

    private String dataA;

    private String dataB;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 中间数据 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 结果 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private String dataC1;

    private String dataC2;

    private String dataD;

    private String dataE;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 结果 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}