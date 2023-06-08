package org.tinygame.legendstory.async;

public interface IAsyncOperation {
    /**
     * 绑定id
     *
     * @return
     */
    default int bindId() {
        return 0;
    }

    /**
     * 执行异步操作
     */
    void doAsync();

    /**
     * 执行完成逻辑
     */
    default void doFinish() {

    }
}
