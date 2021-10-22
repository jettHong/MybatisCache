package org.example.domain;

import lombok.Data;

/**
 * 基础实体域
 * @author jett
 */
@Data
public class BaseDomain extends SuperBaseDomain {
    private String tempField;
    private String baseDomainField;
}
