package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.context.TestContext;
import cc.jinhx.easytool.process.node.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

/**
 * TestChain
 *
 * @author jinhx
 * @since 2022-03-29
 */
@Component
public class TestChain extends AbstractChain<TestContext> {

    @Override
    protected void checkParams(ChainContext<TestContext> chainContext) {

    }

    /**
     * 设置节点信息
     */
    @Override
    protected void setNodeInfo() {
        this.addNodes(new HashSet<>(Arrays.asList(TestGetAByReqNode.class)));
        this.addNodes(new HashSet<>(Arrays.asList(TestGetBByReqNode.class)), 7000);
        this.addNode(TestGetC2ByBNode.class, ChainNode.FailHandleEnum.RETRY, ChainNode.RetryTimesEnum.FIVE);
        this.addNode(TestGetC1ByANode.class, 1000L);
        this.addNode(TestGetDNode.class, ChainNode.FailHandleEnum.ABANDON);
        this.addNode(TestGetEByAllNode.class);
    }

}
