package com.game.boot;

import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableAutoConfiguration
@Order(ManagementServerProperties.BASIC_AUTH_ORDER + 1)  // For Spring Actuator endpoints
public class GameConfiguration extends com.game.configuration.GameConfiguration {

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().
                antMatchers(HttpMethod.GET, "/v2/api-docs");
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.game.controller"))
                .paths(PathSelectors.any())
                .build();
    }

}
