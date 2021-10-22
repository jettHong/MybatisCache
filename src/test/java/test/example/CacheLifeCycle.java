package test.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.domain.Author;
import org.example.util.ReflectionUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Q：缓存生命周期，什么情况下会被销毁。
 * A：
 * 1、session关闭时
 * 2、（同一会话中）有更新语句时，即使更新不是本表本记录。
 * 3、commit提交
 * 4、rollback回滚
 * 5、调用session.clearCache()
 * REF：https://www.imooc.com/video/21492
 * @author jett
 */
@Slf4j
public class CacheLifeCycle {
    
    /**
     * session关闭时
     */
    @Test
    public void testClosing() throws IOException, NoSuchFieldException, IllegalAccessException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
        SqlSession session = sqlSessionFactory.openSession();
        Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
        log.debug(author1.toString());
    
        CachingExecutor cachingExecutor = (CachingExecutor) ReflectionUtils.getPrivateField(session, "executor");
        SimpleExecutor simpleExecutor = (SimpleExecutor) ReflectionUtils.getPrivateField(cachingExecutor, "delegate");
        PerpetualCache localCache = (PerpetualCache) ReflectionUtils.getPrivateFieldIncludeSuper(simpleExecutor, "localCache");
        Map<Object, Object> cache = (Map<Object, Object>) ReflectionUtils.getPrivateField(localCache, "cache");
    
        System.out.println("before close session...");
        System.out.println("cache size:"+cache.size());
        cache.entrySet().stream().forEach(System.out::println);
    
        session.close();
    
        System.out.println("after close session...");
        System.out.println("cache size:"+cache.size());
        cache.entrySet().stream().forEach(System.out::println);
    }
    
    /**
     * 2、更新语句时
     */
    @Test
    public void testUpdate() throws IOException, NoSuchFieldException, IllegalAccessException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author2.toString());
            
            // 经过更新操作后,下面的查询就无缓存,这里的执行的是update方法,但实际SQL内容还是查询语句.
            session.update("org.example.mapper.AuthorMapper.getById", 1);
            
            Author author3 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author3.toString());
        }
    }
    
    /**
     * 3、commit提交
     */
    @Test
    public void testCommit() throws IOException, NoSuchFieldException, IllegalAccessException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author2.toString());
    
            // commit提交操作后，缓存失效
            session.commit();
            
            Author author3 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author3.toString());
        }
    }
    
    /**
     * 4、rollback回滚
     */
    @Test
    public void testRollback() throws IOException, NoSuchFieldException, IllegalAccessException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author2.toString());
            
            // rollback回滚操作后，缓存失效
            session.rollback();
            
            Author author3 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author3.toString());
        }
    }
    
    /**
     * 5、调用session.clearCache()
     */
    @Test
    public void testClearCache() throws IOException, NoSuchFieldException, IllegalAccessException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author2.toString());
            
            // 调用clearCache，使缓存失效
            session.clearCache();
            
            Author author3 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author3.toString());
        }
    }
    
}
