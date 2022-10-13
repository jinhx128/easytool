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
        ChainContext<DemoContext> chainContext = ChainContext.create(DemoContext.class);
        chainContext.getContextInfo().setReq("req");
        ProcessResult<DemoContext> processResult = ChainHandler.execute(DemoChain.class, chainContext);
        System.out.println(processResult);
    }

}