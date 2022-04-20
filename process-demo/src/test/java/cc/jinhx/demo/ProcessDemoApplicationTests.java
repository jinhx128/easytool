package cc.jinhx.demo;

import cc.jinhx.demo.chain.TestNodeChain;
import cc.jinhx.demo.context.TestContext;
import cc.jinhx.process.AbstractLogicHandler;
import cc.jinhx.process.NodeChainContext;
import cc.jinhx.process.ProcessResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProcessDemoApplicationTests {

	@Test
	void Test1() {
		ProcessResult<TestContext> processResult = new AbstractLogicHandler<TestContext>() {

			NodeChainContext<TestContext> testNodeChainContext = builNodeChainContext(TestContext.class);

			@Override
			protected void checkParams() {
				System.out.println("校验参数");
			}

			@Override
			protected ProcessResult<TestContext> process() {
				testNodeChainContext.getContextInfo().setReq("re");
				executeNodeChain(TestNodeChain.class, testNodeChainContext);
				return builSuccessResult(testNodeChainContext.getContextInfo());
			}

		}.execute();
		System.out.println(processResult.getData());
	}

	@Test
	void Test2() {
		ProcessResult<TestContext> processResult = new AbstractLogicHandler<TestContext>() {

			NodeChainContext<TestContext> testNodeChainContext = builNodeChainContext(TestContext.class);

			@Override
			protected void checkParams() {
				System.out.println("校验参数");
			}

			@Override
			protected ProcessResult<TestContext> process() {
				testNodeChainContext.getContextInfo().setReq("req");
				executeNodeChain(TestNodeChain.class, testNodeChainContext);
				return builSuccessResult(testNodeChainContext.getContextInfo());
			}

		}.execute();
		System.out.println(processResult);
	}

}
