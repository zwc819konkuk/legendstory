package org.tinygame.legendstory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.cmdHandler.CmdHandlerFactory;
import org.tinygame.legendstory.cmdHandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 主线程处理器
 */
public final class MainThreadProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    /**
     * 单例对象
     */
    static private final MainThreadProcessor _instance = new MainThreadProcessor();

    /**
     * 创建一个单线程
     */
    static final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
            Thread newThread = new Thread(r);
            newThread.setName("MainThreadProcessor");
            return newThread;
    });

    /**
     * 私有化类默认构造器
     */
    private MainThreadProcessor() {

    }

    /**
     * 获取单例对象
     *
     * @return 主线程服务器
     */
    static public MainThreadProcessor getInstance() {
        return _instance;
    }

    /**
     * 处理消息
     *
     * @param ctx 客户端信道上下文
     * @param msg 消息对象
     */
    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if (null == ctx || null == msg) {
            return;
        }

        this._es.submit(() -> {
            //获取消息类
            Class<?> msgClazz = msg.getClass();

            //获取指令处理器
            ICmdHandler<? extends GeneratedMessageV3>
                    cmdHandler = CmdHandlerFactory.create(msgClazz);

            if (null == cmdHandler) {
                LOGGER.error(
                        "未找到相对应的指令处理器。msgClazz = {}",
                        msgClazz.getName()
                );
                return;
            }

            try {
                //处理指令
                cmdHandler.handle(ctx, cast(msg));
            } catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        });
    }

    /**
     * 转型消息对象
     *
     * @param msg
     * @param <Tcmd>
     * @return
     */
    static private <Tcmd extends GeneratedMessageV3> Tcmd cast(Object msg) {
        if (null == msg) {
            return null;
        } else {
            return (Tcmd) msg;
        }
    }
}
