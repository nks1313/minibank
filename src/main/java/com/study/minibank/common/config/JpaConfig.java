package com.study.minibank.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing 활성화: @CreatedDate, @LastModifiedDate 필드를 자동으로 채워준다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
