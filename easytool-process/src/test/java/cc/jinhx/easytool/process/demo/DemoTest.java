package cc.jinhx.easytool.process.demo;

import cc.jinhx.easytool.process.ProcessResult;
import cc.jinhx.easytool.process.SpringContextConfig;
import cc.jinhx.easytool.process.chain.ChainContext;
import cc.jinhx.easytool.process.chain.ChainHandler;
import cc.jinhx.easytool.process.demo.chain.DemoChain;
import cc.jinhx.easytool.process.demo.context.DemoContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * DemoTest
 *
 * @author jinhx
 * @since 2022-03-21
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContextConfig.class)
public class DemoTest {

    @Test
    public void test1() {
        // 创建上下文
        ChainContext<DemoContext> chainContext = ChainContext.create(DemoContext.class);
        // 设置入参
        chainContext.getContextInfo().setReq("req");

        // 执行指定链路，并返回所有数据
        ProcessResult<DemoContext> processResult1 = ChainHandler.execute(DemoChain.class, chainContext);
        System.out.println(processResult1);

        // 执行指定链路，并返回指定数据
        ProcessResult<String> processResult2 = ChainHandler.execute(DemoChain.class, chainContext, DemoContext::getDataG);
        System.out.println(processResult2);
    }

}