package org.tinygame.legendstory.model;

public class User {
    /**
     * 用户Id
     */
    public int userId;
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


}
