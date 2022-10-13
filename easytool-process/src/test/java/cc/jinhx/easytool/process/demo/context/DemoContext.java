package cc.jinhx.easytool.process.demo.context;

import lombok.Data;

/**
 * DemoContext
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Data
public class DemoContext {

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 入参 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 入参 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    private String req;

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 中间数据 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private String dataA;

    private String dataB;

    private String dataC;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 中间数据 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 结果 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    private String dataD;

    private String dataE;

    private String dataF;

    private String dataG;

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 结果 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

}