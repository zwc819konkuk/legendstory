package org.tinygame.legendstory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.rank.RankService;

import java.util.List;

/**
 * 消费者消费队列
 */
public final class MQConsumer {
    static private final Logger LOGGER = LoggerFactory.getLogger(MQConsumer.class);

    private MQConsumer() {

    }

    static public void init() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("legendstory");
        consumer.setNamesrvAddr("127.0.0.1");
        try {
            consumer.subscribe("Victor", "*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtList, ConsumeConcurrentlyContext ctx) {
                    for (MessageExt msgExt : msgExtList) {
                        VictorMsg mqMsg = JSONObject.parseObject(msgExt.getBody(), VictorMsg.class);
                        LOGGER.info("从消息队列中收到战斗结果，winnerId={},loserId={}",mqMsg.winnerId,mqMsg.loserId);
                        //刷新排行榜
                        RankService.getInstance().refreshRank(mqMsg.winnerId, mqMsg.loserId);
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
