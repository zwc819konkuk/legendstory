package org.tinygame.legendstory.model;

public class User {
    /**
     * 用户Id
     */
    public int userId;
    /**
     * 用户名字
     */
    public String userName;
    /**
     * 英雄形象
     */
    public String heroAvatar;
    /**
     * 当前血量
     */
    public int currHp;
    /**
     * 移动状态
     */
    public final MoveState moveState = new MoveState();

    /**
     * 是否已经死亡
     */
    public boolean died;
}
