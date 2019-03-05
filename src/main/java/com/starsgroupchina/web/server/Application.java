package com.starsgroupchina.web.server;

import com.google.common.collect.Lists;
import com.starsgroupchina.common.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableCaching
@EnableFeignClients(basePackages = {"com.starsgroupchina.web.server", "com.starsgroupchina.common"})
@EnableSwagger2
@EnableScheduling
@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan(basePackages = "com.starsgroupchina.web.server.bean.mapper")
@ComponentScan(basePackages = {"com.starsgroupchina.web.server", "com.starsgroupchina.common"})
@SpringBootApplication
public class Application {

    static {
        Arrays.stream(ErrorCode.class.getDeclaredFields())
                .forEach(p -> {
                    try {
                        ErrorMessage annotation = p.getAnnotation(ErrorMessage.class);
                        System.setProperty(String.valueOf(p.get("value")), annotation.value());
                    } catch (Exception e) {
                        log.error("fail to load error code:  " + p.getName());
                    }
                });
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Docket docket() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        List<Parameter> parameters = Lists.newArrayList();
        parameterBuilder.name("token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build();
        parameters.add(parameterBuilder.build());

        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.starsgroupchina.web.server.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }
}
