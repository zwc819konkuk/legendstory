package org.tinygame.legendstory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.legendstory.MysqlSessionFactory;
import org.tinygame.legendstory.login.db.IUserDao;
import org.tinygame.legendstory.login.db.UserEntity;

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
     * @param userName 用户名称
     * @param password 用户密码
     * @return 用户实体
     */
    public UserEntity userLogin(String userName, String password) {
        if (null == userName || null == password) {
            return null;
        }
        LOGGER.info("current thread = {}",Thread.currentThread().getName());
        try (SqlSession mySqlSession = MysqlSessionFactory.openSession()) {
            //获取Dao
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);

            //获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);

            if (null != userEntity) {
                if (!password.equals(userEntity.password)) {
                    LOGGER.error("用户密码错误，userName = {}", userName);
                    throw new RuntimeException("用户密码错误");
                }
            } else {
                //新建用户实体
                userEntity = new UserEntity();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman";

                //将用户实体插入到数据库
                dao.insertInto(userEntity);
            }
            return userEntity;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }
}
