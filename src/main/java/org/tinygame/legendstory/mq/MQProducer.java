package org.tinygame.legendstory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.GameMsgEncoder;

/**
 * 消息队列生产者
 */
public final class MQProducer {
    static private final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    /**
     * 生产者
     */
    static private DefaultMQProducer _producer = null;

    private MQProducer() {

    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("legendstory");
            producer.setNamesrvAddr("127.0.0.1");
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 发送消息
     *
     * @param topic 主题
     * @param msg   消息对象
     */
    static public void sendMsg(String topic, Object msg) {
        if (null == topic || null == msg) {
            return;
        }
        if (null == _producer) {
            throw new RuntimeException("_producer not init");
        }

        Message mqMsg = new Message();
        mqMsg.setTopic(topic);
        mqMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(mqMsg);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
