package org.tinygame.legendstory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public final class UserManager {
    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new HashMap<>();

    /**
     * 私有化构造器
     */
    private UserManager() {

    }

    /**
     * 添加用户
     *
     * @param newUser
     */
    public static void addUser(User newUser) {
        if (newUser != null) {
            _userMap.put(newUser.userId, newUser);
        }
    }

    /**
     * 根据id移除用户
     *
     * @param userId
     */
    public static void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 用户列表
     * @return
     */
    public static Collection<User> listUser(){
        return _userMap.values();
    }

    /**
     * 根据id得到user
     * @param userId
     * @return
     */
    public static User getUserById(Integer userId) {
        return _userMap.get(userId);
    }
}
