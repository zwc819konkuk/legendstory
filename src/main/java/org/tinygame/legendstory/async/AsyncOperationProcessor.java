package org.tinygame.legendstory.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 */
public final class AsyncOperationProcessor {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);
    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 创建一个单线程
     */
    private final ExecutorService[] _esArray = new ExecutorService[8];

    /**
     * 私有化构造器
     */
    private AsyncOperationProcessor() {
        for (int i = 0;i <_esArray.length;i++){
            final String threadName = "AsyncOperationProcessor" + i;
            _esArray[i] = Executors.newSingleThreadExecutor((newRunnable) -> {
                Thread newThread = new Thread(newRunnable);
                newThread.setName(threadName);
                return newThread;
            });
        }

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理异步操作
     * @param asyncOp
     */
    public void process(IAsyncOperation asyncOp) {
        if (null == asyncOp) {
            return;
        }

        //根据bindId获取索引
        int bindId = Math.abs(asyncOp.bindId());
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(() -> {
            try {
                //执行异步操作
                asyncOp.doAsync();
                //返回主线程完成逻辑
                MainThreadProcessor.getInstance().process(asyncOp :: doFinish);
            } catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        });
    }
}
