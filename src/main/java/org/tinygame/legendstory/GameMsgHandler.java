package org.tinygame.legendstory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的消息处理器
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 客户端信道数组，一定要使用static，否则无法实现群发，保证只有一个
     */
    static private final ChannelGroup CHANNEL_GROUP  = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static private final Map<Integer,User> USER_MAP = new HashMap<>();
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        CHANNEL_GROUP.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("收到客户端消息, msg = {}", msg.getClass().getName()+"msg="+msg);
        if (msg instanceof GameMsgProtocol.UserEntryCmd){
            //从指令对象中获取id和英雄形象
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd)msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();


            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(heroAvatar);

            //将用户加入字典
            User newUser = new User();
            newUser.userId = userId;
            newUser.heroAvator = heroAvatar;
            USER_MAP.put(newUser.userId,newUser);

            //构建结果并群发
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            CHANNEL_GROUP.writeAndFlush(newResult);

        }else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd){
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();
            for (User currUser : USER_MAP.values()){
                if (null != currUser){
                    continue;
                }
                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder =
                        GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.userId);
                userInfoBuilder.setHeroAvatar(currUser.heroAvator);

                resultBuilder.addUserInfo(userInfoBuilder);

            }
            GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
        }else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if (null == userId) {
                return;
            }

            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;

            GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
            CHANNEL_GROUP.writeAndFlush(newResult);
        }
    }
}
