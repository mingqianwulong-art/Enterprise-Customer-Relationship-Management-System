package com.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * CRM 系统启动类
 *
 * @author CRM Team
 */
@SpringBootApplication
@MapperScan("com.crm.**.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class CrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrmApplication.class, args);
        System.out.println("========================================");
        System.out.println("  企业客户关系管理系统(CRM) 启动成功!");
        System.out.println("  接口文档: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }

}
