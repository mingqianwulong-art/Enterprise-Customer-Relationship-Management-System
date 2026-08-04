package com.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 接口文档配置
 * <p>
 * 配置 JWT Bearer 认证方案，支持在 Swagger UI 中点击「Authorize」
 * 输入 Token 后直接调试受保护的接口。
 *
 * @author CRM Team
 */
@Configuration
public class SpringDocConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer JWT";

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
                        .license(new License().name("Apache 2.0")))
                // 全局安全要求：所有接口默认需要 JWT 认证（登录等免认证接口不受影响）
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("输入登录后获取的 JWT Token")));
    }

}
