package com.schibsted.spain.friends.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket apiDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .protocols(Sets.newHashSet("http"))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.schibsted.spain"))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Schibsted Coding challenge")
                .description("Schibsted coding challenge")
                .contact(new Contact("Anyul Rivas", "https://www.github.com/anyulled", "anyul.rivas@schibdsted.com"))
                .version("1.0")
                .build();
    }
}


