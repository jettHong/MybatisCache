package org.example.domain;

import java.util.Date;
import lombok.Data;

/**
 * 作者
 */
@Data
public class Author extends BaseDomain{
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 业务字段
     */
    private String bio;
    
    /**
     * 收藏夹
     */
    private String favouriteSection;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 类型编码
     */
    private String typeCode;
    
    /**
     * 类型名称
     */
    private String typeName;
}
