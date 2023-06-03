package org.tinygame.legendstory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 指令处理器接口
 * @param <Tcmd>
 */
public interface ICmdHandler<Tcmd extends GeneratedMessageV3> {
    /**
     * 处理指令
     * @param ctx
     * @param cmd
     */
    void handle(ChannelHandlerContext ctx, Tcmd cmd);
}
