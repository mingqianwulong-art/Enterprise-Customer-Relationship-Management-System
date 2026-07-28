package com.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 接口文档配置
 *
 * @author CRM Team
 */
@Configuration
public class SpringDocConfig {

    /**
     * 全局 API 分组（扫描所有控制器）
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .displayName("所有接口")
                .packagesToScan("com.crm")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("企业客户关系管理系统 API 文档")
                        .version("1.0.0")
                        .description("CRM 系统接口文档")
                        .contact(new Contact().name("CRM Team"))
                        .license(new License().name("Apache 2.0")));
    }

}
