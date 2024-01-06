package com.example.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accOpenApi() {
        return new OpenAPI()
                .info(setInfo());
    }

    private Info setInfo(){
        Info info = new Info();
        info.setTitle("discover-services");
        info.setDescription("Provides services for discovering AWS EC2 instances and S3 buckets");
        info.setContact(new Contact().name("srinivas").email("123@123.com"));
        info.setVersion("1.0");
        return info;
    }

}