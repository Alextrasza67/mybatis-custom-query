package com.github.config;

import com.alibaba.druid.proxy.jdbc.NClobProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcClob;
import org.h2.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
public class JacksonSerializerConfig {

    @Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
    private String pattern;

    // 方案一
    @Bean
    public LocalDateTimeSerializer localDateTimeDeserializer() {
        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
    }

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void addObjectMapper(){
        registerClobModule();
    }

    /**
     * 配置clob返回时，jackson 序列化问题
     */
    private void registerClobModule() {
        SimpleModule simpleModule = new  SimpleModule();
        //处理查询结果为 JdbcClob 的数据。非使用druid
        simpleModule.addSerializer(JdbcClob.class, new JsonSerializer<JdbcClob>() {
            @Override
            public void serialize(JdbcClob jdbcClob, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                try {
                    jsonGenerator.writeString(IOUtils.readStringAndClose(jdbcClob.getCharacterStream(),1024));
                } catch (SQLException e) {
                    log.error("jdbcClob serialize error ",e);
                }
            }
        });
        //处理查询结果为 NClobProxy 的数据，使用 druid 时有一层封装，nClobProxy.getRawNClob() 实际还是 JdbcClob
        simpleModule.addSerializer(NClobProxy.class, new JsonSerializer<NClobProxy>() {
            @Override
            public void serialize(NClobProxy nClobProxy, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                try {
                    jsonGenerator.writeString(IOUtils.readStringAndClose(nClobProxy.getRawNClob().getCharacterStream(),1024));
                } catch (SQLException e) {
                    log.error("jdbcClob serialize error ",e);
                }
            }
        });
        objectMapper.registerModule(simpleModule);
    }

}