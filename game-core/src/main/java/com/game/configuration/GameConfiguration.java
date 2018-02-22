package com.game.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.game.CustomAuthPrincipalFilter;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@org.springframework.context.annotation.Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = {"com.game"})
@EnableDynamoDBRepositories(basePackages = "com.game.repository")
public abstract class GameConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.
                csrf().disable().
                addFilter(new CustomAuthPrincipalFilter()).
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().
                anonymous().disable();
        http.
                headers().httpStrictTransportSecurity().requestMatcher(request -> true);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Bean
    public AmazonDynamoDBClient amazonDynamoDB(@Value("${com.game.dynamodbendpoint}") String dynamodbEndpoint,
                                               @Value("${com.game.accesskey}")String amazonAWSAccessKey,
                                               @Value("${com.game.secretkey}") String amazonAWSSecretKey){

        return (AmazonDynamoDBClient) AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration(dynamodbEndpoint, "us-west-2")).withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey))).build();
    }

}
