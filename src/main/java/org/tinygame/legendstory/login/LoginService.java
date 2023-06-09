package org.tinygame.legendstory.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.MysqlSessionFactory;
import org.tinygame.legendstory.async.AsyncOperationProcessor;
import org.tinygame.legendstory.async.IAsyncOperation;
import org.tinygame.legendstory.login.db.IUserDao;
import org.tinygame.legendstory.login.db.UserEntity;
import org.tinygame.legendstory.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * 登陆服务
 */
public class LoginService {

    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    static private final LoginService _instance = new LoginService();

    /**
     * 私有化构造器
     */
    private LoginService() {

    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public LoginService getInstance() {
        return _instance;
    }

    /**
     * 用户登录过程
     *
     * @param userName 用户名称
     * @param password 用户密码
     * @param callback 回调函数
     * @return 用户实体
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (null == userName || null == password) {
            return;
        }
        LOGGER.info("current thread = {}", Thread.currentThread().getName());
        IAsyncOperation asyncOp = new AsyncGetUserByName(userName, password) {

            @Override
            public void doFinish() {
                if (null != callback) {
                    callback.apply(this.getUserEntity());
                }
            }
        };
        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 更新redis中的用户基本信息
     * @param userEntity
     */
    private void updateUserBasicInfoInRedis(UserEntity userEntity) {
        if (null == userEntity) {
            return;
        }
        try (Jedis redis = RedisUtil.getRedis()) {
            int userId = userEntity.userId;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId",userId);
            jsonObject.put("userName",userEntity.userName);
            jsonObject.put("heroAvatar",userEntity.heroAvatar);

            //更新redis数据
            redis.hset("User_" + userId, "BasicInfo", jsonObject.toJSONString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private class AsyncGetUserByName implements IAsyncOperation {
        private final String _userName;
        private final String _password;
        private UserEntity _userEntity = null;

        AsyncGetUserByName(String username, String password) {
            _userName = username;
            _password = password;
        }

        public UserEntity getUserEntity() {
            return _userEntity;
        }

        @Override
        public int bindId() {
            return _userName.charAt(_userName.length() - 1);
        }

        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MysqlSessionFactory.openSession()) {
                //获取Dao
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);
                LOGGER.info("current thread = {}", Thread.currentThread().getName());

                //获取用户实体
                UserEntity userEntity = dao.getUserByName(_userName);

                if (null != userEntity) {
                    if (!_password.equals(userEntity.password)) {
                        LOGGER.error("用户密码错误，userName = {}", _userName);
                    }
                } else {
                    //新建用户实体
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman";

                    //将用户实体插入到数据库
                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;

                LoginService.getInstance().updateUserBasicInfoInRedis(userEntity);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

    }
}
