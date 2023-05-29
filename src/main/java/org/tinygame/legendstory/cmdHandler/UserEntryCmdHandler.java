package org.tinygame.legendstory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.legendstory.Broadcaster;
import org.tinygame.legendstory.model.User;
import org.tinygame.legendstory.model.UserManager;
import org.tinygame.legendstory.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd>{
    @Override
    public  void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd msg) {
        // 从指令对象中获取用户 Id 和英雄形象
        GameMsgProtocol.UserEntryCmd cmd = msg;
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        // 将用户加入字典
        User newUser = new User();
        newUser.userId = userId;
        newUser.heroAvatar = heroAvatar;
        UserManager.addUser(newUser);

        // 将用户 Id 附着到 Channel
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        // 构建结果并发送
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
