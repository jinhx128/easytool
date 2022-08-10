package cc.jinhx.easytool.process.chain;

import cc.jinhx.easytool.process.node.*;

import java.util.Arrays;
import java.util.HashSet;

/**
 * TestChain
 *
 * @author jinhx
 * @since 2022-03-29
 */
public class TestChain extends AbstractChain {

    /**
     * 添加节点
     */
    @Override
    protected void setNodeInfo() {
        // 添加一组异步执行的节点
        this.addNodes(new HashSet<>(Arrays.asList(TestGetAByReqNode.class, TestGetBByReqNode.class)));
        // 添加一个异步执行的节点，与下面添加的异步节点属于同组
        this.addNode(TestGetC2ByBNode.class, ChainNode.FailHandleEnum.RETRY, ChainNode.RetryTimesEnum.FIVE);
        this.addNode(TestGetC1ByANode.class, 1000L);
        // 添加一个异步执行的节点，与上面添加的异步节点属于同组
        this.addNode(TestGetDNode.class);
        // 添加一个同步执行的节点，自己属于一个组，且组内只能有自己
        this.addNode(TestGetEByAllNode.class);
    }

}
