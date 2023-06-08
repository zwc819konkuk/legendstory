package org.tinygame.legendstory.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 */
public final class AsyncOperationProcessor {

    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    /**
     * 创建一个单线程
     */
    static private final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
        Thread newThread = new Thread(r);
        newThread.setName("AsyncOperationProcessor");
        return newThread;
    });

    /**
     * 私有化构造器
     */
    private AsyncOperationProcessor() {

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public AsyncOperationProcessor getInstance() {
        return _instance;
    }

    public void process(Runnable r) {
        if (null == r) {
            return;
        }

        _es.submit(r);
    }
}
