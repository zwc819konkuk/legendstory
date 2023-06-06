package org.tinygame.legendstory;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * MySQL 会话工厂
 */
public class MysqlSessionFactory {
    /**
     * Mybatis Sql 会话工厂
     */
    static private SqlSessionFactory _sqlSessionFactory;

    private MysqlSessionFactory(){

    }

    /**
     * 初始化
     */
    static public void init(){
        try {
            _sqlSessionFactory = new SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("MyBatisConfig.xml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启Mysql会话
     * @return
     */
    static public SqlSession openSession(){
        if (null == _sqlSessionFactory){
            throw new RuntimeException("_sqlSessionFactory 尚未初始化");
        }

        return _sqlSessionFactory.openSession(true);
    }
}
