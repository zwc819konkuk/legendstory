package org.tinygame.legendstory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.async.AsyncOperationProcessor;
import org.tinygame.legendstory.login.LoginService;
import org.tinygame.legendstory.login.db.UserEntity;
import org.tinygame.legendstory.model.User;
import org.tinygame.legendstory.model.UserManager;
import org.tinygame.legendstory.msg.GameMsgProtocol;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        LOGGER.info(
                "username = {}, password = {}",
                cmd.getUserName(),
                cmd.getPassword()
        );


        LoginService.getInstance().userLogin(cmd.getUserName(), cmd.getPassword(), (userEntity) -> {
            if (null == userEntity) {
                LOGGER.error("用户登陆失败，userName = {}", cmd.getUserName());
                return null;
            }
            LOGGER.error("当前线程 = {}", Thread.currentThread().getName());
            LOGGER.info("用户登陆成功，userId={} , userName = {}",
                    userEntity.userId,
                    userEntity.userName
            );
            int userId = userEntity.userId;
            String heroAvatar = userEntity.heroAvatar;


            // 新建用户
            User newUser = new User();
            newUser.userId = userId;
            newUser.userName = userEntity.userName;
            newUser.heroAvatar = heroAvatar;
            newUser.currHp = 100;
            UserManager.addUser(newUser);

            // 将用户 Id 附着到 Channel
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();
            resultBuilder.setUserId(newUser.userId);
            resultBuilder.setUserName(newUser.userName);
            resultBuilder.setHeroAvatar(newUser.heroAvatar);

            //构建结果并发送
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });


    }
}
