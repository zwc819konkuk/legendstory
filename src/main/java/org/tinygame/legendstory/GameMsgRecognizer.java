package org.tinygame.legendstory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息识别器
 */
public final class GameMsgRecognizer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    /**
     * 消息代码和消息体字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap<>();
    /**
     * 消息类型和消息代码字典
     */
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化默认构造器
     */
    private GameMsgRecognizer(){}

    static public void init(){

        Class<?>[] innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClazz : innerClazzArray){
            if (!GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }

            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()){
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replaceAll("_","");
                strMsgCode = strMsgCode.toLowerCase();

                if (!strMsgCode.startsWith(clazzName)){
                    continue;
                }

                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("{} <=> {}",innerClazz.getName(),msgCode.getNumber());

                    _msgCodeAndMsgBodyMap.put(msgCode.getNumber(),(GeneratedMessageV3) returnObj);
                    _msgClazzAndMsgCodeMap.put(innerClazz,msgCode.getNumber());
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
        }
    }
    static public Message.Builder getBuilderByMsgCode(int msgCode){
        if (msgCode < 0){
            return null;
        }

        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if (null == msg){
            return null;
        }

        return msg.newBuilderForType();
    }

    static public int getMsgCodeByMsgClazz(Class<?> msgClazz){
        if (null == msgClazz){
            return -1;
        }

        Integer msgCode = _msgClazzAndMsgCodeMap.get(msgClazz);
        if (null != msgCode){
            return msgCode.intValue();
        } else {
            return -1;
        }
    }
}
