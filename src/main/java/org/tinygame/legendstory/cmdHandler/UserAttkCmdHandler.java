package org.tinygame.legendstory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.legendstory.Broadcaster;
import org.tinygame.legendstory.model.User;
import org.tinygame.legendstory.model.UserManager;
import org.tinygame.legendstory.mq.MQProducer;
import org.tinygame.legendstory.mq.VictorMsg;
import org.tinygame.legendstory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd>{
    /**
     * 用户攻击指令处理器
     * @param ctx
     * @param cmd
     */
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx || null == cmd){
            return;
        }
        //获取攻击者Id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("useId")).get();
        if (null == attkUserId){
            return;
        }
        //获取被攻击者Id
        int targetUserId = cmd.getTargetUserId();

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        //获取被攻击用户
        User targetUser = UserManager.getUserById(targetUserId);
        if (null == targetUser){
            return;
        }

        int subtractHp = 10;
        targetUser.currHp = targetUser.currHp - subtractHp;

        //广播减血消息
        broadcastSubtractHp(targetUserId,subtractHp);

        //广播死亡消息
        if (targetUser.currHp <= 0){
            broadcastDie(targetUserId);
            if (!targetUser.died){
                targetUser.died = true;
                VictorMsg mqMsg = new VictorMsg();
                mqMsg.winnerId = attkUserId;
                mqMsg.loserId = targetUserId;
                MQProducer.sendMsg("Victor",mqMsg);
            }
        }
    }


    /**
     * 广播减血消息
     * @param targetUserId 目标用户id
     * @param subtractHp 减血量
     */
    static private void broadcastSubtractHp(int targetUserId,int subtractHp){
        if (targetUserId <= 0 || subtractHp <= 0){
            return;
        }
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播死亡消息
     * @param targetUserId
     */
    static private void broadcastDie(int targetUserId){
        if (targetUserId <= 0){
            return;
        }
        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
