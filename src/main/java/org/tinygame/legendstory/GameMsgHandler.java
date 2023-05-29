package org.tinygame.legendstory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.cmdHandler.ICmdHandler;
import org.tinygame.legendstory.cmdHandler.UserEntryCmdHandler;
import org.tinygame.legendstory.cmdHandler.UserMoveToCmdHandler;
import org.tinygame.legendstory.cmdHandler.WhoElseIsHereCmdHandler;

import org.tinygame.legendstory.model.UserManager;
import org.tinygame.legendstory.msg.GameMsgProtocol;


/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        super.handlerRemoved(ctx);
        Broadcaster.removeChannel(ctx.channel());

        // 先拿到用户 Id
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userId) {
            return;
        }

        UserManager.removeUserById(userId);

        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
        resultBuilder.setQuitUserId(userId);

        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        LOGGER.info(
                "收到客户端消息, msgClazz = {}, msgObj = {}",
                msg.getClass().getName(),
                msg
        );

        ICmdHandler<? extends GeneratedMessageV3> cmdHandler = null;

        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            new UserEntryCmdHandler().handle(ctx, (GameMsgProtocol.UserEntryCmd) msg);
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            new WhoElseIsHereCmdHandler().handle(ctx, (GameMsgProtocol.WhoElseIsHereCmd) msg);
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            new UserMoveToCmdHandler().handle(ctx, (GameMsgProtocol.UserMoveToCmd) msg);
        }
    }
}
