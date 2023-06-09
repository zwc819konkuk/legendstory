package org.tinygame.legendstory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.legendstory.Broadcaster;
import org.tinygame.legendstory.model.User;
import org.tinygame.legendstory.model.UserManager;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd>{
    @Override
    public  void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {

        if (null == ctx || null == cmd){
            return;
        }

        // 获取用户 Id 和英雄形象
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null == userId){
            return;
        }

        //获取已存在的用户
        User existUser = UserManager.getUserById(userId);
        if (null ==existUser){
            return;
        }

        String heroAvatar = existUser.heroAvatar;
        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        // 构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
