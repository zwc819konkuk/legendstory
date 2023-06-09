package org.tinygame.legendstory.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.async.AsyncOperationProcessor;
import org.tinygame.legendstory.async.IAsyncOperation;
import org.tinygame.legendstory.login.LoginService;
import org.tinygame.legendstory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {
    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    static private final RankService _instance = new RankService();

    private RankService() {

    }

    static public RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排名列表
     *
     * @param callback 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (null == callback) {
            return;
        }

        IAsyncOperation asynOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asynOp);
    }

    /**
     * 异步方式获取排名
     */
    private class AsyncGetRank implements IAsyncOperation {

        private List<RankItem> _rankItemList = null;

        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getRedis()) {
                //获取字符串集合
                List<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);
                List<RankItem> rankItemList = new ArrayList<>();
                int rankId = 0;

                for (Tuple t : valSet) {
                    //获取用户id
                    int userId = Integer.parseInt(t.getElement());

                    //获取用户的基本信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (null == jsonStr || jsonStr.isEmpty()) {
                        continue;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                    RankItem newItem = new RankItem();
                    newItem.userId = userId;
                    newItem.userName = jsonObject.getString("userName");
                    newItem.heroAvatar = jsonObject.getString("heroAvatar");
                    newItem.win = (int) t.getScore();
                    newItem.rankId = rankId++;

                    rankItemList.add(newItem);
                }
                _rankItemList = rankItemList;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 刷新排行榜
     * @param winnerId 赢家id
     * @param loserId 输家id
     */
    public void refreshRank(int winnerId,int loserId){
        try (Jedis redis = RedisUtil.getRedis()){
            //增加用户的输赢次数
            redis.hincrBy("User_"+winnerId,"win",1);
            redis.hincrBy("User_"+loserId,"lose",1);

            //看看赢家赢了多少次
            String winStr = redis.hget("User_"+winnerId,"win");
            int winInt = Integer.parseInt(winStr);

            redis.zadd("rank",winInt,String.valueOf(winnerId));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }
}
