package org.tinygame.legendstory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.GameMsgEncoder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis实用工具
 */
public final class RedisUtil {
    static private final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    /**
     * redis连接池
     */
    static private JedisPool _jedisPool = null;

    private RedisUtil() {

    }

    static public void init() {
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * get redis instance
     *
     * @return
     */
    static public Jedis getRedis() {
        if (null == _jedisPool) {
            throw new RuntimeException("_jedisPool not init");
        }

        Jedis redis = _jedisPool.getResource();
        return redis;
    }
}
