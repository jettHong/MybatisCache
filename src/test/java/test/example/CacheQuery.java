package test.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.domain.Author;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Q：MyBatis一级缓存生效的必要条件有哪些？
 * A：同时满足以下条件：
 * 1、要求查询的Statementld必须相同，否则无法命中缓存，即使两个查询语句、参数等完全一样。
 * 2、要求传递给SQL的查询参数必须相同。（值相同即可，不需要考虑传入值的外包装）
 * 3、要求分页参数必须相同，否则无法命中缓存。缓存粒度是整个分页查询结果,而不是结果中的每个对象。
 * 4、要求传递给JDBC的SQL必须完全相同。
 * 5、要求执行环境必需相同。
 * REF：https://www.imooc.com/video/21491
 * @author jett
 */
@Slf4j
public class CacheQuery {
    
    /**
     * 验证有缓存的情况
     * @throws IOException
     */
    @Test
    public void sameQuery() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById", 1);
            log.debug(author2.toString());
        }
    }
    
    /**
     * 要求查询的Statementld必须相同，否则无法命中缓存，即使两个查询语句、参数等完全一样。
     * @throws IOException
     */
    @Test
    public void testStatementId() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById1", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById2", 1);
            log.debug(author2.toString());
        }
    }
    
    /**
     * 要求传递给SQL的查询参数必须相同。（值相同即可，不需要考虑传入值的外包装）
     * @throws IOException
     */
    @Test
    public void testParam() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            // 值不同，执行无缓存（废话）
            Author author1 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById1", 1);
            log.debug(author1.toString());
            Author author2 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById1", 2);
            log.debug(author2.toString());
            
            //（最终传到JDBC的值相同即可，不需要考虑传入值的外包装）,执行结果一样有缓存
            Map param = new HashMap<String, Object>();
            param.put("id", "1");
            param.put("cond", "XXXX");
            Author author3 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById3", param);
            log.debug(author3.toString());
            param.put("id", "1");
            param.put("cond", "YYY");
            Author author4 = (Author) session.selectOne("org.example.mapper.AuthorMapper.getById3", param);
            log.debug(author4.toString());
        }
    }
    
    /**
     * 要求分页参数必须相同，否则无法命中缓存。缓存粒度是整个分页查询结果,而不是结果中的每个对象。
     * @throws IOException
     */
    @Test
    public void testPage() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            RowBounds RowBounds1 = RowBounds.DEFAULT;
            List<Author> list1 = session.selectList("org.example.mapper.AuthorMapper.list", null, RowBounds1);
            list1.stream().forEach(System.out::println);
    
            RowBounds RowBounds2 = new RowBounds(0, 99);
            List<Author> list2 = session.selectList("org.example.mapper.AuthorMapper.list", null, RowBounds2);
            list2.stream().forEach(System.out::println);
        }
    }
    
    /**
     * 要求传递给JDBC的SQL必须完全相同。（同一statement，根据条件动态语句DynamicSQL级成不同语句，不能被缓存，废话）
     * @throws IOException
     */
    @Test
    public void testContext() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Map param = new HashMap<String, Object>();
            param.put("id", "1");
            param.put("cond", 1);
            List<Author> list1 = session.selectList("org.example.mapper.AuthorMapper.list", param, RowBounds.DEFAULT);
            list1.stream().forEach(System.out::println);
            param.put("cond", 2);
            List<Author> list2 = session.selectList("org.example.mapper.AuthorMapper.list", param, RowBounds.DEFAULT);
            list2.stream().forEach(System.out::println);
        }
    }
}
