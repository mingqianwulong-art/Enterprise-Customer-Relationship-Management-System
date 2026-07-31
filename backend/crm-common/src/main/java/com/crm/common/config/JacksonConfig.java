package com.crm.common.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局日期序列化配置
 * <p>
 * 仅定制序列化（后端返回前端的格式）为中文格式：
 * - LocalDateTime -> yyyy年MM月dd日 HH:mm:ss
 * - LocalDate     -> yyyy年MM月dd日
 * <p>
 * 反序列化（前端传入后端）保持 Jackson 默认的 ISO 解析，
 * 因此前端 el-date-picker 的 value-format="YYYY-MM-DD" 仍可正常工作。
 *
 * @author CRM
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy年MM月dd日 HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy年MM月dd日";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        };
    }
}
